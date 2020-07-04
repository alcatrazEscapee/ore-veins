/*
 * Part of the Realistic Ore Veins Mod by AlcatrazEscapee
 * Work under Copyright. See the project LICENSE.md for details.
 */

package com.alcatrazescapee.oreveins.world.vein;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.minecraft.block.BlockState;
import net.minecraft.util.JSONUtils;

import com.alcatrazescapee.oreveins.util.collections.IWeightedList;

public class Indicator
{
    private final int maxDepth;
    private final boolean replaceSurface;
    private final int rarity;
    private final boolean ignoreLiquids;

    private final IWeightedList<BlockState> states;
    private final List<BlockState> underStates;

    private Indicator(int maxDepth, boolean replaceSurface, int rarity, boolean ignoreLiquids, IWeightedList<BlockState> states, List<BlockState> underStates)
    {
        this.maxDepth = maxDepth;
        this.replaceSurface = replaceSurface;
        this.rarity = rarity;
        this.ignoreLiquids = ignoreLiquids;
        this.states = states;
        this.underStates = underStates;
    }

    public BlockState getStateToGenerate(Random random)
    {
        return states.get(random);
    }

    public boolean validUnderState(BlockState state)
    {
        return underStates.isEmpty() || underStates.contains(state);
    }

    public int getMaxDepth()
    {
        return maxDepth;
    }

    public boolean shouldReplaceSurface()
    {
        return replaceSurface;
    }

    public int getRarity()
    {
        return rarity;
    }

    public boolean shouldIgnoreLiquids()
    {
        return ignoreLiquids;
    }

    public enum Deserializer implements JsonDeserializer<Indicator>
    {
        INSTANCE;

        @Override
        public Indicator deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            if (!json.isJsonObject())
            {
                throw new JsonParseException("Indicator must be a JSON Object");
            }
            JsonObject obj = json.getAsJsonObject();
            int maxDepth = JSONUtils.getInt(obj, "max_depth", 32);
            if (maxDepth <= 0)
            {
                throw new JsonParseException("Max depth must be > 0");
            }
            int rarity = JSONUtils.getInt(obj, "rarity", 10);
            if (rarity <= 0)
            {
                throw new JsonParseException("Rarity must be > 0");
            }
            boolean ignoreLiquids = JSONUtils.getBoolean(obj, "ignore_liquids", false);
            IWeightedList<BlockState> states = context.deserialize(obj.get("blocks"), new TypeToken<IWeightedList<BlockState>>() {}.getType());
            if (states.isEmpty())
            {
                throw new JsonParseException("Block states cannot be empty!");
            }
            List<BlockState> underStates = obj.has("blocks_under") ? context.deserialize(obj.get("blocks_under"), new TypeToken<List<BlockState>>() {}.getType()) : Collections.emptyList();
            boolean replaceSurface = JSONUtils.getBoolean(obj, "replace_surface", false);
            return new Indicator(maxDepth, replaceSurface, rarity, ignoreLiquids, states, underStates);
        }
    }
}
