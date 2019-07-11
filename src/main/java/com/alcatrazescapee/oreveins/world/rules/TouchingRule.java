/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.world.rules;

import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import com.alcatrazescapee.oreveins.api.IRule;

@ParametersAreNonnullByDefault
public class TouchingRule implements IRule
{
    private Predicate<BlockState> blockMatcher;
    private int minMatches, maxMatches;

    private TouchingRule(Predicate<BlockState> blockMatcher, int minMatches, int maxMatches)
    {
        this.blockMatcher = blockMatcher;
        this.minMatches = minMatches;
        this.maxMatches = maxMatches;
    }

    @Override
    public boolean test(IBlockReader world, BlockPos pos)
    {
        int matchCount = 0;
        for (Direction face : Direction.values())
        {
            if (blockMatcher.test(world.getBlockState(pos.offset(face))))
            {
                matchCount++;
            }
            if (minMatches <= matchCount && matchCount <= maxMatches)
            {
                return true;
            }
        }
        return false;
    }

    public static final class Factory implements IRule.Factory<TouchingRule>
    {
        @Override
        @Nonnull
        public TouchingRule parse(JsonObject json, JsonDeserializationContext context)
        {
            BlockState stateToMatch = context.deserialize(json.get("block"), BlockState.class);
            Predicate<BlockState> blockMatcher = state -> state == stateToMatch;
            int min = JSONUtils.getInt(json, "min", 1);
            int max = JSONUtils.getInt(json, "max", 8);
            return new TouchingRule(blockMatcher, min, max);
        }
    }
}
