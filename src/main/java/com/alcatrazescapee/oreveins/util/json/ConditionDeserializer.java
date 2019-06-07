package com.alcatrazescapee.oreveins.util.json;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.*;

import com.alcatrazescapee.oreveins.api.ICondition;
import com.alcatrazescapee.oreveins.conditions.ConditionTouching;

public class ConditionDeserializer implements JsonDeserializer<ICondition>
{
    private final Map<String, ICondition.Factory<? extends ICondition>> types = new HashMap<>();

    public ConditionDeserializer()
    {
        types.put("touching", new ConditionTouching.Factory());
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
