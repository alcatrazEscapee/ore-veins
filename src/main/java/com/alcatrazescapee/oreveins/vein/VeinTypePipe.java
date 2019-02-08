/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.vein;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.math.BlockPos;

import com.typesafe.config.Config;

@ParametersAreNonnullByDefault
public class VeinTypePipe extends VeinType
{
    public VeinTypePipe(String name, Config config)
    {
        super(name, config);
    }

    @Override
    float getChanceToGenerate(Vein vein, BlockPos pos)
    {
        float sizeMod = verticalSize * vein.getSize();
        if (Math.abs(vein.getPos().getY() - pos.getY()) < sizeMod * 0.7f)
        {
            return 0.005f * this.density;
        }
        else
        {
            return 0.005f * this.density * (1f - Math.abs(vein.getPos().getY() - pos.getY()) / sizeMod * 1.3f);
        }
    }
}
