/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.world.vein;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.math.BlockPos;

/**
 * Used for veins that don't require any type parameters
 *
 * @author AlcatrazEscapee
 */
@ParametersAreNonnullByDefault
public abstract class SimpleVeinType extends VeinType<Vein<?>>
{
    protected SimpleVeinType(Builder builder)
    {
        super(builder);
    }

    @Nonnull
    @Override
    public Vein<SimpleVeinType> createVein(int chunkX, int chunkZ, Random rand)
    {
        BlockPos pos = defaultStartPos(chunkX, chunkZ, rand);
        return new Vein<>(this, pos, rand);
    }
}
