/*
 * Part of the Realistic Ore Veins Mod by AlcatrazEscapee
 * Work under Copyright. See the project LICENSE.md for details.
 */

package com.alcatrazescapee.oreveins.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.common.util.Lazy;

import com.alcatrazescapee.oreveins.Config;
import com.alcatrazescapee.oreveins.world.vein.Indicator;
import com.alcatrazescapee.oreveins.world.vein.Vein;
import com.alcatrazescapee.oreveins.world.vein.VeinManager;
import com.alcatrazescapee.oreveins.world.vein.VeinType;

import static net.minecraft.world.gen.Heightmap.Type.OCEAN_FLOOR_WG;
import static net.minecraft.world.gen.Heightmap.Type.WORLD_SURFACE_WG;

public class VeinsFeature extends Feature<NoFeatureConfig>
{
    private static final Random RANDOM = new Random();
    private static int CHUNK_RADIUS = 0;

    public static void resetChunkRadius()
    {
        CHUNK_RADIUS = 1 + VeinManager.INSTANCE.getVeins().stream().mapToInt(VeinType::getChunkRadius).max().orElse(0) + Config.COMMON.extraChunkRange.get();
    }

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
                    type.createVeins(veins, chunkX, chunkZ, RANDOM);
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
        // Get all nearby veins, filtering out those which are in the wrong dimension
        List<Vein<?>> veins = getNearbyVeins(pos.getX() >> 4, pos.getZ() >> 4, worldIn.getSeed(), CHUNK_RADIUS)
            .stream()
            .filter(vein -> vein.getType().matchesDimension(worldIn.getDimension().getType()))
                .collect(Collectors.toList());
        for (int x = pos.getX(); x < 16 + pos.getX(); x++)
        {
            for (int z = pos.getZ(); z < 16 + pos.getZ(); z++)
            {
                // Do checks here that are specific to the the horizontal position, not the vertical one
                // We load the biome only once and cache it for lazy purposes
                BlockPos biomePos = new BlockPos(x, 0, z);
                Lazy<Biome> lazyBiome = Lazy.of(() -> worldIn.getBiome(biomePos));

                // Then we perform the same checks for each vein
                for (Vein<?> vein : veins)
                {
                    if (vein.getType().matchesBiome(lazyBiome) && vein.inRange(x, z))
                    {
                        Indicator veinIndicator = vein.getType().getIndicator(rand);
                        boolean canGenerateIndicator = false;

                        for (int y = vein.getType().getMinY(); y <= vein.getType().getMaxY(); y++)
                        {
                            BlockPos posAt = new BlockPos(x, y, z);
                            if (rand.nextFloat() < vein.getChanceToGenerate(posAt))
                            {
                                if (vein.getType().canGenerateAt(worldIn, posAt))
                                {
                                    BlockState oreState = vein.getStateToGenerate(pos, rand);
                                    setBlockState(worldIn, posAt, oreState);
                                    if (veinIndicator != null && !canGenerateIndicator)
                                    {
                                        Heightmap.Type heightmap = veinIndicator.shouldIgnoreLiquids() ? OCEAN_FLOOR_WG : WORLD_SURFACE_WG;
                                        int depth = worldIn.getHeight(heightmap, x, z) - y;
                                        if (depth < 0)
                                        {
                                            depth = -depth;
                                        }
                                        canGenerateIndicator = depth < veinIndicator.getMaxDepth();
                                    }
                                }
                            }
                        }

                        if (veinIndicator != null && canGenerateIndicator)
                        {
                            if (rand.nextInt(veinIndicator.getRarity()) == 0)
                            {
                                Heightmap.Type heightmap = veinIndicator.shouldIgnoreLiquids() ? OCEAN_FLOOR_WG : WORLD_SURFACE_WG;
                                BlockPos posAt = worldIn.getHeight(heightmap, new BlockPos(x, 0, z));

                                BlockState indicatorState = veinIndicator.getStateToGenerate(rand);
                                BlockState stateAt = worldIn.getBlockState(posAt);

                                // This happens after, as we replace what was the "under_state"
                                if (veinIndicator.shouldReplaceSurface())
                                {
                                    posAt = posAt.down();
                                }
                                if (indicatorState.isValidPosition(worldIn, posAt) && (veinIndicator.shouldIgnoreLiquids() || !stateAt.getMaterial().isLiquid()) && veinIndicator.validUnderState(worldIn.getBlockState(posAt.down())))
                                {
                                    setBlockState(worldIn, posAt, indicatorState);
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
