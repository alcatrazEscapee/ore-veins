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
public class PipeVeinType extends DefaultVeinType
{
    @Override
    public float getChanceToGenerate(DefaultVein vein, BlockPos pos)
    {
        float sizeMod = verticalSize * vein.getSize();
        if (Math.abs(vein.getPos().getY() - pos.getY()) < sizeMod * 0.7f)
        {
            return 0.005f * density;
        }
        else
        {
            return 0.005f * density * (1f - Math.abs(vein.getPos().getY() - pos.getY()) / sizeMod * 1.3f);
        }
    }
}
