/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.util.json;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.*;
import net.minecraft.block.state.IBlockState;

public class BlockStateListDeserializer implements JsonDeserializer<List<IBlockState>>
{
    @Override
    public List<IBlockState> deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException
    {
        if (json.isJsonPrimitive() || json.isJsonObject())
        {
            final IBlockState state = context.deserialize(json, IBlockState.class);
            return Collections.singletonList(state);
        }
        else if (json.isJsonArray())
        {
            final JsonArray array = json.getAsJsonArray();
            final List<IBlockState> states = new ArrayList<>();
            for (JsonElement element : array)
            {
                states.add(context.deserialize(element, IBlockState.class));
            }
            return states;
        }
        throw new JsonParseException("Unable to parse IBlockState List");
    }
}
