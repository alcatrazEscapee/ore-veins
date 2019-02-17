/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.world;

import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.BasePlacement;
import net.minecraft.world.gen.placement.IPlacementConfig;

/**
 * A placement to call the feature to generate directly at a chunk position
 */
@ParametersAreNonnullByDefault
public class AtChunk extends BasePlacement<IPlacementConfig>
{
    @Override
    public <C extends IFeatureConfig> boolean generate(IWorld world, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, Random random, BlockPos pos, IPlacementConfig placementConfig, Feature<C> feature, C featureConfig)
    {
        feature.func_212245_a(world, chunkGenerator, random, pos, featureConfig);
        return true;
    }
}
