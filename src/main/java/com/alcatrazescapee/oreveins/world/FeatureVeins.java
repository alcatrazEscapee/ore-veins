/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import com.alcatrazescapee.oreveins.OreVeinsConfig;
import com.alcatrazescapee.oreveins.api.IVein;
import com.alcatrazescapee.oreveins.api.IVeinType;
import com.alcatrazescapee.oreveins.vein.Indicator;
import com.alcatrazescapee.oreveins.vein.VeinRegistry;

import static net.minecraft.world.gen.Heightmap.Type.WORLD_SURFACE_WG;

@ParametersAreNonnullByDefault
public class FeatureVeins extends Feature<NoFeatureConfig>
{
    private static final Random RANDOM = new Random();
    private static int CHUNK_RADIUS = 0;

    public static void resetChunkRadius()
    {
        CHUNK_RADIUS = 1 + VeinRegistry.getVeins().stream().mapToInt(IVeinType::getChunkRadius).max().orElse(0) + OreVeinsConfig.INSTANCE.extraChunkRange;
    }

    @Nonnull
    public static List<IVein> getNearbyVeins(int chunkX, int chunkZ, long worldSeed, int radius)
    {
        List<IVein> veins = new ArrayList<>();
        for (int x = chunkX - radius; x <= chunkX + radius; x++)
        {
            for (int z = chunkZ - radius; z <= chunkZ + radius; z++)
            {
                RANDOM.setSeed(worldSeed + x * 341873128712L + z * 132897987541L);
                getVeinsAtChunk(veins, x, z, worldSeed);
            }
        }
        return veins;
    }

    private static void getVeinsAtChunk(List<IVein> veins, int chunkX, int chunkZ, long worldSeed)
    {
        Random random = new Random(worldSeed + chunkX * 341873128712L + chunkZ * 132897987541L);
        for (IVeinType type : VeinRegistry.getVeins())
        {
            for (int i = 0; i < type.getCount(); i++)
            {
                if (random.nextInt(type.getRarity()) == 0)
                {
                    IVein vein = type.createVein(chunkX, chunkZ, random);
                    veins.add(vein);
                }
            }
        }
    }

    @Override
    public boolean place(IWorld world, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, Random random, BlockPos pos, NoFeatureConfig config)
    {
        List<IVein> veins = getNearbyVeins(pos.getX() >> 4, pos.getZ() >> 4, world.getSeed(), CHUNK_RADIUS);
        if (veins.isEmpty()) return false;

        for (IVein vein : veins)
        {
            // todo: dimension checks
            //if (vein.getType().matchesDimension(world.getDimension()))
            {
                generate(world, random, pos.getX(), pos.getZ(), vein);
            }
        }
        return true;
    }

    private void generate(IWorld world, Random random, int xOff, int zOff, IVein<?> vein)
    {
        for (int x = xOff; x < 16 + xOff; x++)
        {
            for (int z = zOff; z < 16 + zOff; z++)
            {
                // Do checks here that are specific to the the horizontal position, not the vertical one
                Biome biomeAt = world.getBiome(new BlockPos(x, 0, z));
                if (vein.getType().matchesBiome(biomeAt) && vein.inRange(x, z))
                {
                    Indicator veinIndicator = vein.getType().getIndicator();
                    boolean canGenerateIndicator = false;

                    for (int y = vein.getType().getMinY(); y <= vein.getType().getMaxY(); y++)
                    {
                        BlockPos posAt = new BlockPos(x, y, z);
                        if (random.nextFloat() < vein.getChanceToGenerate(posAt))
                        {
                            IBlockState stoneState = world.getBlockState(posAt);
                            if (vein.getType().canGenerateIn(stoneState))
                            {
                                IBlockState oreState = vein.getType().getStateToGenerate(random);
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
                            // todo: checks for vegetation?
                            BlockPos posAt = world.getHeight(Heightmap.Type.WORLD_SURFACE_WG, new BlockPos(x, 0, z));

                            IBlockState indicatorState = veinIndicator.getStateToGenerate(random);
                            IBlockState stateAt = world.getBlockState(posAt);

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
