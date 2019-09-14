/*
 * Part of the Realistic Ore Veins Mod by AlcatrazEscapee
 * Work under Copyright. See the project LICENSE.md for details.
 */

package com.alcatrazescapee.oreveins.world;

import java.util.Random;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;

/**
 * A placement to call the feature to generate directly at a chunk position
 */
@ParametersAreNonnullByDefault
public class AtChunk extends Placement<IPlacementConfig>
{
    public AtChunk()
    {
        super(NoPlacementConfig::deserialize);
    }

    @Override
    @Nonnull
    public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generatorIn, Random random, IPlacementConfig configIn, BlockPos pos)
    {
        return Stream.of(pos);
    }
}
