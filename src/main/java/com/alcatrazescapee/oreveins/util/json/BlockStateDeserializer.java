/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.util.json;

import java.lang.reflect.Type;
import java.util.Optional;

import com.google.gson.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IProperty;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockStateDeserializer implements JsonDeserializer<BlockState>
{
    private static BlockState getDefaultState(String blockName) throws JsonParseException
    {
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
        if (block == null)
        {
            throw new JsonParseException("Unrecognized Block: " + blockName);
        }
        return block.getDefaultState();
    }

    private static <T extends Comparable<T>> BlockState withProperty(BlockState base, JsonObject obj, IProperty<T> prop)
    {
        String propName = prop.getName();
        if (obj.has(propName) && obj.get(propName).isJsonPrimitive())
        {
            Optional<T> propValue = prop.parseValue(obj.get(propName).getAsString());
            if (propValue.isPresent())
            {
                return base.with(prop, propValue.get());
            }
        }
        return base;
    }

    @Override
    public BlockState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        if (json.isJsonPrimitive())
        {
            String name = json.getAsString();
            return getDefaultState(name);
        }
        else if (json.isJsonObject())
        {
            JsonObject jsonObj = json.getAsJsonObject();
            String name = jsonObj.get("block").getAsString();
            BlockState state = getDefaultState(name);
            for (IProperty<?> prop : state.getProperties())
            {
                state = withProperty(state, jsonObj, prop);
            }
            return state;
        }
        throw new JsonParseException("IBlockState must be String or Object");
    }
}
