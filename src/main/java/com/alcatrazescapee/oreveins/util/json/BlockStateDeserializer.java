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
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

public class BlockStateDeserializer implements JsonDeserializer<IBlockState>
{
    @SuppressWarnings("deprecation")
    private static IBlockState getBlockState(String name, int meta) throws JsonParseException
    {
        Block block = Block.getBlockFromName(name);
        if (block == null)
        {
            throw new JsonParseException("Unrecognized Block: " + name);
        }
        return meta == -1 ? block.getDefaultState() : block.getStateFromMeta(meta);
    }

    @SuppressWarnings("deprecation")
    private static IBlockState getOre(String oreName) throws JsonParseException
    {
        NonNullList<ItemStack> ores = OreDictionary.getOres(oreName, false);
        for (ItemStack oreStack : ores)
        {
            if (!oreStack.isEmpty())
            {
                Block block = Block.getBlockFromItem(oreStack.getItem());
                if (block != Blocks.AIR)
                {
                    int meta = oreStack.getMetadata();
                    return block.getStateFromMeta(meta);
                }
            }
        }
        throw new JsonParseException("Unrecognized Ore Dictionary Name: " + oreName);
    }

    @Override
    public IBlockState deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException
    {
        if (json.isJsonObject())
        {
            JsonObject obj = json.getAsJsonObject();
            if (obj.has("block"))
            {
                String name = obj.get("block").getAsString();
                if (obj.has("meta"))
                {
                    return getBlockState(name, obj.get("meta").getAsInt());
                }
                return getBlockState(name, -1);
            }
            else if (obj.has("ore"))
            {
                return getOre(obj.get("ore").getAsString());
            }
        }
        else if (json.isJsonPrimitive())
        {
            String name = json.getAsString();
            if (name.startsWith("ore:"))
            {
                return getOre(name.substring(4));
            }
            return getBlockState(name, -1);
        }
        throw new JsonParseException("Unable to parse IBlockState");
    }
}
