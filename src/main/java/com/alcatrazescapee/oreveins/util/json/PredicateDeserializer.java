/*
 * Part of the Realistic Ore Veins Mod by AlcatrazEscapee
 * Work under Copyright. See the project LICENSE.md for details.
 */

package com.alcatrazescapee.oreveins.util.json;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.google.gson.*;
import net.minecraft.util.JSONUtils;

public abstract class PredicateDeserializer<E, T extends Predicate<E>> implements JsonDeserializer<T>
{
    private final Class<T> elementType;
    private final String collectionName;

    public PredicateDeserializer(Class<T> elementType, String collectionName)
    {
        this.elementType = elementType;
        this.collectionName = collectionName;
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        if (json.isJsonObject())
        {
            JsonObject obj = json.getAsJsonObject();
            String typeName = JSONUtils.getString(obj, "type");
            switch (typeName)
            {
                case "and":
                    return createCollectionRule(JSONUtils.getJsonArray(obj, collectionName), context, false);
                case "or":
                    return createCollectionRule(JSONUtils.getJsonArray(obj, collectionName), context, true);
                case "not":
                    T innerRule = context.deserialize(JSONUtils.getJsonObject(obj, collectionName), elementType);
                    innerRule.test(null);
                    return createPredicate(item -> !innerRule.test(item));
                default:
                    return createSingleRule(obj, typeName);
            }
        }
        else if (json.isJsonArray())
        {
            // Default to or rule
            return createCollectionRule(json.getAsJsonArray(), context, true);
        }
        else if (json.isJsonPrimitive())
        {
            return createSingleRule(json.getAsString());
        }
        throw new JsonParseException("Rule should be an object, array, or string");
    }

    protected T createSingleRule(JsonObject json, String typeName) throws JsonParseException
    {
        throw new JsonParseException("Unknown type for rule: " + typeName);
    }

    protected abstract T createSingleRule(String name) throws JsonParseException;

    protected abstract T createPredicate(Predicate<E> predicate);

    private T createCollectionRule(JsonArray array, JsonDeserializationContext context, boolean baseValue)
    {
        List<T> innerRules = new ArrayList<>();
        for (JsonElement arrayElement : array)
        {
            innerRules.add(context.deserialize(arrayElement, elementType));
        }
        return createPredicate(item -> {
            for (T rule : innerRules)
            {
                if (rule.test(item) == baseValue)
                {
                    return baseValue;
                }
            }
            return !baseValue;
        });
    }
}
