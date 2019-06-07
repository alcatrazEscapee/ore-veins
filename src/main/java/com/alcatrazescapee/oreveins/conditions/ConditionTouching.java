package com.alcatrazescapee.oreveins.conditions;

import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.alcatrazescapee.oreveins.api.ICondition;

@ParametersAreNonnullByDefault
public class ConditionTouching implements ICondition
{
    private Predicate<IBlockState> blockMatcher;
    private int minMatches, maxMatches;

    private ConditionTouching(Predicate<IBlockState> blockMatcher, int minMatches, int maxMatches)
    {
        this.blockMatcher = blockMatcher;
        this.minMatches = minMatches;
        this.maxMatches = maxMatches;
    }

    @Override
    public boolean test(World world, BlockPos pos)
    {
        int matchCount = 0;
        for (EnumFacing face : EnumFacing.values())
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

    public static final class Factory implements ICondition.Factory<ConditionTouching>
    {
        @Override
        @Nonnull
        public ConditionTouching parse(JsonObject json, JsonDeserializationContext context)
        {
            IBlockState stateToMatch = context.deserialize(json.get("block"), IBlockState.class);
            Predicate<IBlockState> blockMatcher = state -> state == stateToMatch;
            int min = JsonUtils.getInt(json, "min", 1);
            int max = JsonUtils.getInt(json, "max", 8);
            return new ConditionTouching(blockMatcher, min, max);
        }
    }
}
