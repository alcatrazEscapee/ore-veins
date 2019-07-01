/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.api;

import java.util.function.BiPredicate;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@ParametersAreNonnullByDefault
public interface ICondition extends BiPredicate<World, BlockPos>
{
    @Override
    boolean test(World world, BlockPos pos);

    interface Factory<T extends ICondition>
    {
        @Nonnull
        T parse(JsonObject json, JsonDeserializationContext context);
    }
}
