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
    private final Map<String, Class<? extends IVeinType>> types = new HashMap<>();

    public VeinTypeDeserializer()
    {
        // If you add more vein types, remember to add a map entry here
        types.put("sphere", VeinTypeSphere.class);
        types.put("cluster", VeinTypeCluster.class);
        types.put("cone", VeinTypeCone.class);
        types.put("pipe", VeinTypePipe.class);
        types.put("curve", VeinTypeCurve.class);
    }

    @Override
    public IVeinType<?> deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject obj = json.getAsJsonObject();
        String veinType = obj.get("type").getAsString();
        if (types.containsKey(veinType))
        {
            return context.deserialize(json, types.get(veinType));
        }
        throw new JsonParseException("Unknown vein type " + veinType);
    }
}
