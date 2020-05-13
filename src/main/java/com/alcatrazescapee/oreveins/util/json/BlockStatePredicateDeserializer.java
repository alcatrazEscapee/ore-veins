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
import com.google.gson.reflect.TypeToken;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public enum BlockStatePredicateDeserializer implements JsonDeserializer<Predicate<BlockState>>
{
    INSTANCE;

    @Override
    public Predicate<BlockState> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        if (json.isJsonArray())
        {
            List<Predicate<BlockState>> subPredicates = new ArrayList<>();
            for (JsonElement subElement : json.getAsJsonArray())
            {
                subPredicates.add(context.deserialize(subElement, new TypeToken<Predicate<BlockState>>() {}.getType()));
            }
            return stateIn -> subPredicates.stream().anyMatch(predicate -> predicate.test(stateIn));
        }
        else if (json.isJsonObject())
        {
            JsonObject obj = json.getAsJsonObject();
            return parsePredicate(obj.get("block").getAsString());
        }
        else
        {
            return parsePredicate(json.getAsString());
        }
    }

    public Predicate<BlockState> parsePredicate(String value)
    {
        if (value.startsWith("#"))
        {
            String tagName = value.substring(1);
            Tag<Block> tag = BlockTags.getCollection().get(new ResourceLocation(tagName));
            if (tag != null)
            {
                return stateIn -> tag.contains(stateIn.getBlock());
            }
            else
            {
                throw new JsonParseException("Unknown tag: " + tag);
            }
        }
        else
        {
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(value));
            if (block != null)
            {
                return stateIn -> stateIn.getBlock() == block;
            }
            else
            {
                throw new JsonParseException("Unknown block: " + value);
            }
        }
    }
}
