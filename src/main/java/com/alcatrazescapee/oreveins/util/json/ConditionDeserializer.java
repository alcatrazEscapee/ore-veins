/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.util.json;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.*;

import com.alcatrazescapee.oreveins.api.ICondition;
import com.alcatrazescapee.oreveins.conditions.TouchingCondition;

public class ConditionDeserializer implements JsonDeserializer<ICondition>
{
    private final Map<String, ICondition.Factory<? extends ICondition>> types = new HashMap<>();

    public ConditionDeserializer()
    {
        types.put("touching", new TouchingCondition.Factory());
    }

    @Override
    public ICondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject obj = json.getAsJsonObject();
        String conditionType = obj.get("type").getAsString();
        if (types.containsKey(conditionType))
        {
            return types.get(conditionType).parse(obj, context);
        }
        throw new JsonParseException("Unknown condition type " + conditionType);
    }
}
