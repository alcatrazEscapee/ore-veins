/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.util.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockStateDeserializer implements JsonDeserializer<IBlockState>
{
    @Override
    public IBlockState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        String name;
        if (json.isJsonPrimitive())
        {
            name = json.getAsString();
        }
        else if (json.isJsonObject())
        {
            name = json.getAsJsonObject().get("block").getAsString();

        }
        else
        {
            throw new JsonParseException("IBlockState must be JsonPrimitive or JsonObject");
        }
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
        if (block == null)
        {
            throw new JsonParseException("Unrecognized Block: " + name);
        }
        // todo: figure out metadata / state properties handling
        return block.getDefaultState();
    }
}
