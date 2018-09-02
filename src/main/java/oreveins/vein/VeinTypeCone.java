/*
 *  Part of the Ore Veins Mod by alcatrazEscapee
 *  Work under Copyright. Licensed under the GPL-3.0.
 *  See the project LICENSE.md for more information.
 */

package oreveins.vein;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.math.BlockPos;

import com.typesafe.config.Config;
import oreveins.util.ConfigHelper;

@ParametersAreNonnullByDefault
public class VeinTypeCone extends VeinType
{
    private final float coneFactor;
    private final boolean inverted;

    public VeinTypeCone(Config config)
    {
        super(config);
        this.coneFactor = ConfigHelper.getValue(config, "shape", 0.5f);
        this.inverted = ConfigHelper.getBoolean(config, "inverted", false);
    }

    @Override
    float getChanceToGenerate(Vein vein, BlockPos pos)
    {
        final double dx = Math.pow(vein.getPos().getX() - pos.getX(), 2);
        final double dz = Math.pow(vein.getPos().getZ() - pos.getZ(), 2);

        float dy = 0.5f + (pos.getY() - vein.getPos().getY()) / (this.verticalSize * vein.getSize() * 2f); // 0 at bottom, 1.0 at top
        if (inverted) dy = 1f - dy;
        if (dy > 1f || dy < 0f)
            return 0;

        final float maxR = (1f - this.coneFactor * dy) * this.horizontalSize * vein.getSize();
        return 0.005f * this.density * (1.0f - (float) (dx + dz) / (maxR * maxR)); // Otherwise calculate from radius
    }
}
