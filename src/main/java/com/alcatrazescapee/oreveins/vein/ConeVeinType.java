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

@SuppressWarnings({"unused", "WeakerAccess"})
@ParametersAreNonnullByDefault
public class ConeVeinType extends DefaultVeinType
{
    float shape = 0.5f;
    boolean inverted = false;

    @Override
    public float getChanceToGenerate(DefaultVein vein, BlockPos pos)
    {
        final double dx = Math.pow(vein.getPos().getX() - pos.getX(), 2);
        final double dz = Math.pow(vein.getPos().getZ() - pos.getZ(), 2);

        float dy = 0.5f + (pos.getY() - vein.getPos().getY()) / (verticalSize * vein.getSize() * 2f); // 0 at bottom, 1.0 at top
        if (inverted) dy = 1f - dy;
        if (dy > 1f || dy < 0f)
            return 0;

        final float maxR = (1f - shape * dy) * horizontalSize * vein.getSize();
        return 0.005f * density * (1.0f - (float) (dx + dz) / (maxR * maxR)); // Otherwise calculate from radius
    }
}
