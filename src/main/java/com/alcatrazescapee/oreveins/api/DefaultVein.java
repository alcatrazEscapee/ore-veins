/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.api;

import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.math.BlockPos;

/**
 * A default vein implementation. Used for veins that don't require any additional instance parameters
 *
 * @author AlcatrazEscapee
 */
@SuppressWarnings("WeakerAccess")
@ParametersAreNonnullByDefault
public class DefaultVein extends AbstractVein<DefaultVeinType>
{
    public DefaultVein(DefaultVeinType type, BlockPos pos, Random rand)
    {
        super(type, pos, rand);
    }

    @Override
    public boolean inRange(int x, int z)
    {
        return getType().inRange(this, pos.getX() - x, pos.getZ() - z);
    }

    @Override
    public double getChanceToGenerate(BlockPos pos)
    {
        return getType().getChanceToGenerate(this, pos);
    }
}
