/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.util.json;

import java.lang.reflect.Type;

import com.google.gson.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class BlockStateDeserializer implements JsonDeserializer<IBlockState>
{
    @Override
    public IBlockState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        if (json.isJsonObject())
        {
            JsonObject obj = json.getAsJsonObject();
            String name = obj.get("block").getAsString();
            if (obj.has("meta"))
            {
                return getBlockState(name, obj.get("meta").getAsInt());
            }
            return getBlockState(name, -1);
        }
        else if (json.isJsonPrimitive())
        {
            String name = json.getAsString();
            return getBlockState(name, -1);
        }
        throw new JsonParseException("Unable to parse IBlockState");
    }

    @SuppressWarnings("deprecation")
    private IBlockState getBlockState(String name, int meta) throws JsonParseException
    {
        Block block = Block.getBlockFromName(name);
        if (block == null)
        {
            throw new JsonParseException("Unrecognized block name!");
        }
        return meta == -1 ? block.getDefaultState() : block.getStateFromMeta(meta);
    }
}
