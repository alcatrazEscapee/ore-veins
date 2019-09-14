/*
 * Part of the Realistic Ore Veins Mod by AlcatrazEscapee
 * Work under Copyright. See the project LICENSE.md for details.
 */

package com.alcatrazescapee.oreveins.world.vein;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.BlockPos;

@ParametersAreNonnullByDefault
public class ConeVeinType extends SimpleVeinType
{
    private final float shape;
    private final boolean inverted;

    public ConeVeinType(JsonObject obj, JsonDeserializationContext context) throws JsonParseException
    {
        super(obj, context);
        shape = JSONUtils.getFloat(obj, "shape", 0.5f);
        inverted = JSONUtils.getBoolean(obj, "inverted", false);
    }

    @Override
    public float getChanceToGenerate(Vein<?> vein, BlockPos pos)
    {
        final double dx = Math.pow(vein.getPos().getX() - pos.getX(), 2);
        final double dz = Math.pow(vein.getPos().getZ() - pos.getZ(), 2);

        float dy = 0.5f + (pos.getY() - vein.getPos().getY()) / (verticalSize * vein.getSize() * 2f); // 0 at bottom, 1.0 at top
        if (inverted)
        {
            dy = 1f - dy;
        }
        if (dy > 1f || dy < 0f)
        {
            return 0;
        }

        final float maxR = (1f - shape * dy) * horizontalSize * vein.getSize();
        return 0.005f * density * (1.0f - (float) (dx + dz) / (maxR * maxR)); // Otherwise calculate from radius
    }
}
