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
public interface IDimensionRule extends Predicate<DimensionType>
{
    IDimensionRule DEFAULT = dim -> dim == DimensionType.OVERWORLD;

    class Deserializer extends PredicateDeserializer<DimensionType, IDimensionRule>
    {
        public static final Deserializer INSTANCE = new Deserializer();

        private Deserializer()
        {
            super(IDimensionRule.class, "dimensions");
        }

        @Override
        protected IDimensionRule createSingleRule(String name)
        {
            final ResourceLocation typeName = new ResourceLocation(name);
            return type -> typeName.equals(type.getRegistryName());
        }

        @Override
        protected IDimensionRule createPredicate(Predicate<DimensionType> predicate)
        {
            return predicate::test;
        }
    }
}
