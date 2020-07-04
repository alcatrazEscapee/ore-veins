/*
 * Part of the Realistic Ore Veins Mod by AlcatrazEscapee
 * Work under Copyright. See the project LICENSE.md for details.
 */

package com.alcatrazescapee.oreveins.world.vein;

import java.util.Collection;
import java.util.Random;
import java.util.function.Predicate;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import com.alcatrazescapee.oreveins.util.collections.IWeightedList;

public abstract class SingleVeinType<V extends Vein<?>> extends VeinType<V>
{
    protected final Predicate<BlockState> stoneStates;
    protected final IWeightedList<BlockState> oreStates;

    protected SingleVeinType(JsonObject json, JsonDeserializationContext context) throws JsonParseException
    {
        super(json, context);

        stoneStates = context.deserialize(json.get("stone"), new TypeToken<Predicate<BlockState>>() {}.getType());
        oreStates = context.deserialize(json.get("ore"), new TypeToken<IWeightedList<BlockState>>() {}.getType());
        if (oreStates.isEmpty())
        {
            throw new JsonParseException("Ore States cannot be empty.");
        }
    }

    public BlockState getStateToGenerate(Random rand)
    {
        return oreStates.get(rand);
    }

    public Collection<BlockState> getOreStates()
    {
        return oreStates.values();
    }

    @Override
    public boolean canGenerateAt(IBlockReader world, BlockPos pos)
    {
        BlockState stoneState = world.getBlockState(pos);
        return stoneStates.test(stoneState) && super.canGenerateAt(world, pos);
    }
}
