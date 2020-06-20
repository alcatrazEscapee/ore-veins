/*
 * Part of the Realistic Ore Veins Mod by AlcatrazEscapee
 * Work under Copyright. See the project LICENSE.md for details.
 */

package com.alcatrazescapee.oreveins.world.rule;

import java.util.function.Predicate;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.ForgeRegistries;

import com.alcatrazescapee.oreveins.util.json.PredicateDeserializer;

@FunctionalInterface
public interface BiomeRule extends Predicate<Biome>
{
    BiomeRule DEFAULT = biome -> true;

    class Deserializer extends PredicateDeserializer<Biome, BiomeRule>
    {
        public static final Deserializer INSTANCE = new Deserializer();

        private Deserializer()
        {
            super(BiomeRule.class, "biomes");
        }

        @Override
        protected BiomeRule createSingleRule(JsonObject json, String typeName)
        {
            if ("tag".equals(typeName))
            {
                final BiomeDictionary.Type type = BiomeDictionary.Type.getType(JSONUtils.getString(json, "biomes"));
                return biome -> BiomeDictionary.getTypes(biome).contains(type);
            }
            else if ("biome".equals(typeName))
            {
                return createSingleRule(json.get("biomes").getAsString());
            }
            else
            {
                throw new JsonParseException("Type must be logical (and, or, not), or (biome, tag)");
            }
        }

        @Override
        protected BiomeRule createSingleRule(String name) throws JsonParseException
        {
            // Assume a single biome entry
            Biome biomeIn = ForgeRegistries.BIOMES.getValue(new ResourceLocation(name));
            if (biomeIn == null)
            {
                throw new JsonParseException("Unknown biome: " + name);
            }
            return biomeIn::equals;
        }

        @Override
        protected BiomeRule createPredicate(Predicate<Biome> predicate)
        {
            return predicate::test;
        }
    }
}
