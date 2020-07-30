/*
 * Part of the Realistic Ore Veins Mod by AlcatrazEscapee
 * Work under Copyright. See the project LICENSE.md for details.
 */

package com.alcatrazescapee.oreveins.world.rule;

import java.util.function.Predicate;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;

import com.alcatrazescapee.oreveins.util.json.PredicateDeserializer;

@FunctionalInterface
public interface DimensionRule extends Predicate<DimensionType>
{
    DimensionRule DEFAULT = dim -> dim == DimensionType.OVERWORLD;

    class Deserializer extends PredicateDeserializer<DimensionType, DimensionRule>
    {
        public static final Deserializer INSTANCE = new Deserializer();

        private Deserializer()
        {
            super(DimensionRule.class, "dimensions");
        }

        @Override
        protected DimensionRule createSingleRule(String name)
        {
            final ResourceLocation typeName = new ResourceLocation(name);
            return type -> typeName.equals(type.getRegistryName());
        }

        @Override
        protected DimensionRule createPredicate(Predicate<DimensionType> predicate)
        {
            return predicate::test;
        }
    }
}
