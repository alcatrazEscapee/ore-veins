/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.vein;


import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.math.BlockPos;

import com.alcatrazescapee.oreveins.api.DefaultVein;
import com.alcatrazescapee.oreveins.api.DefaultVeinType;

@SuppressWarnings({"unused"})
@ParametersAreNonnullByDefault
public class SphereVeinType extends DefaultVeinType
{
    @Override
    public float getChanceToGenerate(DefaultVein vein, BlockPos pos)
    {
        final double dx = Math.pow(vein.getPos().getX() - pos.getX(), 2);
        final double dy = Math.pow(vein.getPos().getY() - pos.getY(), 2);
        final double dz = Math.pow(vein.getPos().getZ() - pos.getZ(), 2);

        final float radius = (float) ((dx + dz) / (horizontalSize * horizontalSize * vein.getSize()) +
                dy / (verticalSize * verticalSize * vein.getSize()));
        return 0.005f * density * (1.0f - radius);
    }
}
