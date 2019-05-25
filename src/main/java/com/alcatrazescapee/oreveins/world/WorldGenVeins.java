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

import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import com.alcatrazescapee.oreveins.OreVeinsConfig;
import com.alcatrazescapee.oreveins.api.IVein;
import com.alcatrazescapee.oreveins.api.IVeinType;
import com.alcatrazescapee.oreveins.vein.Indicator;
import com.alcatrazescapee.oreveins.vein.VeinRegistry;

public class WorldGenVeins implements IWorldGenerator
{
    private static final Random RANDOM = new Random();
    private static int CHUNK_RADIUS = 0;

    public static void resetChunkRadius()
    {
        CHUNK_RADIUS = 4 + VeinRegistry.getVeins().stream().mapToInt(IVeinType::getChunkRadius).max().orElse(0) + OreVeinsConfig.EXTRA_CHUNK_SEARCH_RANGE;
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

    private static BlockPos getTopBlockIgnoreVegetation(World world, BlockPos pos)
    {
        Chunk chunk = world.getChunkFromBlockCoords(pos);
        BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos(pos.getX(), chunk.getTopFilledSegment() + 16, pos.getZ());
        while (mPos.getY() > 0)
        {
            mPos.move(EnumFacing.DOWN, 1);
            IBlockState state = chunk.getBlockState(mPos);
            if (state.getMaterial().blocksMovement() && !state.getBlock().isLeaves(state, world, mPos) && !state.getBlock().isFoliage(world, mPos) && !state.getMaterial().isLiquid() && !(state.getBlock() instanceof BlockHugeMushroom))
            {
                break;
            }
        }
        return mPos.move(EnumFacing.UP).toImmutable();
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        List<IVein> veins = getNearbyVeins(chunkX, chunkZ, world.getSeed(), CHUNK_RADIUS);
        if (veins.isEmpty()) return;

        int xoff = chunkX * 16 + 8;
        int zoff = chunkZ * 16 + 8;
        for (IVein vein : veins)
        {
            if (vein.getType().matchesDimension(world.provider.getDimension()))
            {
                generate(world, random, xoff, zoff, vein);
            }
        }
    }

    private void generate(World world, Random random, int xOff, int zOff, IVein<?> vein)
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
                            // At this point, shift the vein upwards if using relative y position
                            if (vein.getType().useRelativeY())
                            {
                                posAt = getTopBlockIgnoreVegetation(world, posAt).up(y);
                            }
                            IBlockState stoneState = world.getBlockState(posAt);
                            if (vein.getType().canGenerateIn(stoneState))
                            {
                                IBlockState oreState = vein.getType().getStateToGenerate(random);
                                world.setBlockState(posAt, oreState);
                                if (veinIndicator != null && !canGenerateIndicator)
                                {
                                    int depth = world.getHeight(x, z) - y;
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
                            BlockPos posAt = veinIndicator.shouldIgnoreVegetation() ? getTopBlockIgnoreVegetation(world, new BlockPos(x, 0, z)) : new BlockPos(x, world.getHeight(x, z), z);

                            IBlockState indicatorState = veinIndicator.getStateToGenerate(random);
                            IBlockState stateAt = world.getBlockState(posAt);

                            // The indicator must pass canPlaceBlockAt
                            // The previous state must be replaceable, non-liquid or the vein ignores liquids
                            // The under state must pass validUnderState
                            if (indicatorState.getBlock().canPlaceBlockAt(world, posAt) && stateAt.getBlock().isReplaceable(world, posAt) &&
                                    (veinIndicator.shouldIgnoreLiquids() || !stateAt.getMaterial().isLiquid()) &&
                                    veinIndicator.validUnderState(world.getBlockState(posAt.down())))
                            {
                                world.setBlockState(posAt, indicatorState);
                            }
                        }
                    }
                }
            }
        }
    }

}
