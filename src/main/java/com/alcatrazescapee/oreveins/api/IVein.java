/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.math.BlockPos;

/**
 * This is a vein instance. It is created from a {@link IVeinType} to create a ore vein in the world
 * When creating veins, recommend follow one of two patterns:
 * 1. Create a vein type and vein implementation:
 * {@code class NewVein extends AbstractVein<NewVeinType>} and {@code class NewVeinType extends AbstractVeinType<VeinType>}
 * or 2. Create a single vein type:
 * {@code class NewVeinType extends DefaultVeinType}
 *
 * @param <T> The implementing vein type
 *
 * @author AlcatrazEscapee
 */
@ParametersAreNonnullByDefault
public interface IVein<T extends IVeinType<?>>
{
    /**
     * Gets the centre position of this vein
     *
     * @return a block pos
     */
    @Nonnull
    BlockPos getPos();

    /**
     * Gets the type of this vein instance
     *
     * @return a vein type
     */
    T getType();

    /**
     * Checks if the vein is in range of a point.
     * This should typically call {@code getType().inRange()}
     *
     * @param x absolute x position
     * @param z absolute z position
     * @return if the vein can generate at any y position in that column
     */
    boolean inRange(int x, int z);

    /**
     * Gets the chance to generate at a position
     * This should typically call {@code getType().getChanceToGenerate()}
     *
     * @param pos a position to generate at
     * @return a chance, with <=0 meaning no chance, >= 1 indicating 100% chance
     */
    double getChanceToGenerate(BlockPos pos);

}
