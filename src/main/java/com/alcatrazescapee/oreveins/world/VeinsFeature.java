/*
 * Part of the Realistic Ore Veins Mod by AlcatrazEscapee
 * Work under Copyright. See the project LICENSE.md for details.
 */

package com.alcatrazescapee.oreveins.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import com.alcatrazescapee.oreveins.Config;
import com.alcatrazescapee.oreveins.world.vein.Indicator;
import com.alcatrazescapee.oreveins.world.vein.Vein;
import com.alcatrazescapee.oreveins.world.vein.VeinManager;
import com.alcatrazescapee.oreveins.world.vein.VeinType;

import static net.minecraft.world.gen.Heightmap.Type.WORLD_SURFACE_WG;

@ParametersAreNonnullByDefault
public class VeinsFeature extends Feature<NoFeatureConfig>
{
    private static final Random RANDOM = new Random();
    private static int CHUNK_RADIUS = 0;

    public static void resetChunkRadius()
    {
        CHUNK_RADIUS = 1 + VeinManager.INSTANCE.getVeins().stream().mapToInt(VeinType::getChunkRadius).max().orElse(0) + Config.SERVER.extraChunkRange.get();
    }

    @Nonnull
    public static List<Vein<?>> getNearbyVeins(int chunkX, int chunkZ, long worldSeed, int radius)
    {
        List<Vein<?>> veins = new ArrayList<>();
        for (int x = chunkX - radius; x <= chunkX + radius; x++)
        {
            for (int z = chunkZ - radius; z <= chunkZ + radius; z++)
            {
                getVeinsAtChunk(veins, x, z, worldSeed);
            }
        }
        return veins;
    }

    private static void getVeinsAtChunk(List<Vein<?>> veins, int chunkX, int chunkZ, long worldSeed)
    {
        RANDOM.setSeed(worldSeed + chunkX * 341873128712L + chunkZ * 132897987541L);
        for (VeinType<?> type : VeinManager.INSTANCE.getVeins())
        {
            for (int i = 0; i < type.getCount(); i++)
            {
                if (RANDOM.nextInt(type.getRarity()) == 0)
                {
                    Vein<?> vein = type.createVein(chunkX, chunkZ, RANDOM);
                    veins.add(vein);
                }
            }
        }
    }

    public VeinsFeature()
    {
        super(NoFeatureConfig::deserialize);
    }

    @Override
    public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config)
    {
        List<Vein<?>> veins = getNearbyVeins(pos.getX() >> 4, pos.getZ() >> 4, worldIn.getSeed(), CHUNK_RADIUS);
        if (veins.isEmpty()) return false;

        for (Vein<?> vein : veins)
        {
            if (vein.getType().matchesDimension(worldIn.getDimension()))
            {
                generate(worldIn, rand, pos.getX(), pos.getZ(), vein);
            }
        }
        return true;
    }

    private void generate(IWorld world, Random random, int xOff, int zOff, Vein<?> vein)
    {
        for (int x = xOff; x < 16 + xOff; x++)
        {
            for (int z = zOff; z < 16 + zOff; z++)
            {
                // Do checks here that are specific to the the horizontal position, not the vertical one
                Biome biomeAt = world.getBiome(new BlockPos(x, 0, z));
                if (vein.getType().matchesBiome(biomeAt) && vein.inRange(x, z))
                {
                    Indicator veinIndicator = vein.getType().getIndicator(random);
                    boolean canGenerateIndicator = false;

                    for (int y = vein.getType().getMinY(); y <= vein.getType().getMaxY(); y++)
                    {
                        BlockPos posAt = new BlockPos(x, y, z);
                        if (random.nextFloat() < vein.getChanceToGenerate(posAt))
                        {
                            if (vein.getType().canGenerateAt(world, posAt))
                            {
                                BlockState oreState = vein.getType().getStateToGenerate(random);
                                setBlockState(world, posAt, oreState);
                                if (veinIndicator != null && !canGenerateIndicator)
                                {
                                    int depth = world.getHeight(WORLD_SURFACE_WG, x, z) - y;
                                    if (depth < 0) depth = -depth;
                                    canGenerateIndicator = depth < veinIndicator.getMaxDepth();
                                }
                            }
                        }
                    }

                    if (veinIndicator != null && canGenerateIndicator)
                    {
                        if (random.nextInt(veinIndicator.getRarity()) == 0)
                        {
                            BlockPos posAt = world.getHeight(Heightmap.Type.WORLD_SURFACE_WG, new BlockPos(x, 0, z));

                            BlockState indicatorState = veinIndicator.getStateToGenerate(random);
                            BlockState stateAt = world.getBlockState(posAt);

                            // The indicator must pass canPlaceBlockAt
                            // The previous state must be replaceable, non-liquid or the vein ignores liquids
                            // The under state must pass validUnderState
                            if (indicatorState.isValidPosition(world, posAt) &&
                                    (veinIndicator.shouldIgnoreLiquids() || !stateAt.getMaterial().isLiquid()) &&
                                    veinIndicator.validUnderState(world.getBlockState(posAt.down())))
                            {
                                setBlockState(world, posAt, indicatorState);
                            }
                        }
                    }
                }
            }
        }
    }

}
