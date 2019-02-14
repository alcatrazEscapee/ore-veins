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

import com.alcatrazescapee.oreveins.api.IVeinType;
import com.alcatrazescapee.oreveins.vein.*;

public class VeinTypeDeserializer implements JsonDeserializer<IVeinType<?>>
{
    private static final Map<String, Class<? extends IVeinType>> TYPES = new HashMap<>();

    static
    {
        // If you add more vein types, remember to add a map entry here
        TYPES.put("sphere", VeinTypeSphere.class);
        TYPES.put("cluster", VeinTypeCluster.class);
        TYPES.put("cone", VeinTypeCone.class);
        TYPES.put("pipe", VeinTypePipe.class);
        TYPES.put("curve", VeinTypeCurve.class);
    }

    @Override
    public IVeinType<?> deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject obj = json.getAsJsonObject();
        String veinType = obj.get("type").getAsString();
        if (TYPES.containsKey(veinType))
        {
            return context.deserialize(json, TYPES.get(veinType));
        }
        throw new JsonParseException("Unknown vein type " + veinType);
    }
}
