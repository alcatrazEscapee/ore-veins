/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.world.rule;

import java.util.function.Predicate;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;

import com.alcatrazescapee.oreveins.util.json.PredicateDeserializer;

@FunctionalInterface
public interface DimensionRule extends Predicate<Dimension>
{
    DimensionRule DEFAULT = dim -> dim.getType() == DimensionType.OVERWORLD;

    class Deserializer extends PredicateDeserializer<Dimension, DimensionRule>
    {
        public static final Deserializer INSTANCE = new Deserializer();

        private Deserializer()
        {
            super(DimensionRule.class, "dimensions");
        }

        @Override
        protected DimensionRule createSingleRule(String name)
        {
            // Assume a single biome entry
            final ResourceLocation biomeName = new ResourceLocation(name);
            return biome -> biomeName.equals(biome.getType().getRegistryName());
        }

        @Override
        protected DimensionRule createPredicate(Predicate<Dimension> predicate)
        {
            return predicate::test;
        }
    }
}
