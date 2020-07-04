/*
 * Part of the Realistic Ore Veins Mod by AlcatrazEscapee
 * Work under Copyright. See the project LICENSE.md for details.
 */

package com.alcatrazescapee.oreveins.util.json;

import java.lang.reflect.Type;

import com.google.gson.*;
import net.minecraft.util.JSONUtils;

import com.alcatrazescapee.oreveins.util.collections.IWeightedList;
import com.alcatrazescapee.oreveins.util.collections.WeightedList;

public class WeightedListDeserializer<T> implements JsonDeserializer<IWeightedList<T>>
{
    private final Class<T> elementClass;

    public WeightedListDeserializer(Class<T> elementClass)
    {
        this.elementClass = elementClass;
    }

    @Override
    public IWeightedList<T> deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException
    {
        if (json.isJsonPrimitive() || json.isJsonObject())
        {
            T state = context.deserialize(json, elementClass);
            return IWeightedList.singleton(state);
        }
        else if (json.isJsonArray())
        {
            JsonArray array = json.getAsJsonArray();
            IWeightedList<T> states = new WeightedList<>();
            for (JsonElement element : array)
            {
                if (element.isJsonObject())
                {
                    JsonObject obj = element.getAsJsonObject();
                    float weight = JSONUtils.getFloat(obj, "weight", 1);
                    states.add(weight, context.deserialize(element, elementClass));
                }
                else
                {
                    states.add(1, context.deserialize(element, elementClass));
                }
            }
            return states;
        }
        throw new JsonParseException("Unable to parse Weighted List of " + elementClass.getSimpleName());
    }
}
