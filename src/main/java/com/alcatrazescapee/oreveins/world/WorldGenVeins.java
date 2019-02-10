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
    // This is the max chunk radius that is searched when trying to gather new veins
    // The larger this is, the larger veins can be (as blocks from them will generate in chunks that are farther away)
    // Make sure that veins won't try and go beyond this, it can cause strange generation issues. (chunks missing, cut off, etc.)
    private static int CHUNK_RADIUS;

    public static void resetSearchRadius()
    {
        int maxRadius = VeinRegistry.getVeins().stream().mapToInt(IVeinType::getChunkRadius).max().orElse(0);
        CHUNK_RADIUS = 1 + maxRadius + OreVeinsConfig.EXTRA_CHUNK_SEARCH_RANGE;
    }

    @Nonnull
    public static List<IVein> getNearbyVeins(int chunkX, int chunkZ, long worldSeed, int radius)
    {
        List<IVein> veins = new ArrayList<>();

        for (IVeinType type : VeinRegistry.getVeins())
        {
            for (int x = chunkX - radius; x <= chunkX + radius; x++)
            {
                for (int z = chunkZ - radius; z <= chunkX + radius; z++)
                {
                    RANDOM.setSeed(worldSeed + x * 341873128712L + z * 132897987541L);
                    type.addVeins(veins, x, z, RANDOM);
                }
            }
        }
        return veins;
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

    private void generate(World world, Random random, int xOff, int zOff, IVein vein)
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
