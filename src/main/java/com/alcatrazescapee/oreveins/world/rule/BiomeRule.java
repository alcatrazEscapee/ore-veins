/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.world.rule;

import java.util.function.Predicate;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

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
                final BiomeDictionary.Type type = BiomeDictionary.Type.getType(typeName);
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
        protected BiomeRule createSingleRule(String name)
        {
            // Assume a single biome entry
            final ResourceLocation biomeName = new ResourceLocation(name);
            return biome -> biomeName.equals(biome.getRegistryName());
        }

        @Override
        protected BiomeRule createPredicate(Predicate<Biome> predicate)
        {
            return predicate::test;
        }
    }
}
