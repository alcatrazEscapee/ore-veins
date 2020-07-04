/*
 * Part of the Realistic Ore Veins Mod by AlcatrazEscapee
 * Work under Copyright. See the project LICENSE.md for details.
 */

package com.alcatrazescapee.oreveins.util.json;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.*;
import net.minecraft.util.JSONUtils;

import com.alcatrazescapee.oreveins.world.vein.*;

public enum VeinTypeDeserializer implements JsonDeserializer<VeinType<?>>
{
    INSTANCE;

    private final Map<String, Factory> factories;

    VeinTypeDeserializer()
    {
        factories = new HashMap<>();

        factories.put("cluster", ClusterVeinType::new);
        factories.put("sphere", SphereVeinType::new);
        factories.put("cone", ConeVeinType::new);
        factories.put("pipe", PipeVeinType::new);
        factories.put("curve", CurveVeinType::new);
    }

    @Override
    public VeinType<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject obj = json.getAsJsonObject();
        String veinTypeName = JSONUtils.getString(obj, "type");
        Factory factory = factories.get(veinTypeName);
        if (factory == null)
        {
            throw new JsonParseException("Unknown Vein Type: " + veinTypeName);
        }
        return factory.create(json.getAsJsonObject(), context);
    }

    /**
     * The factory interface that wraps Vein Type constructors
     */
    interface Factory
    {
        VeinType<?> create(JsonObject json, JsonDeserializationContext context);
    }
}
