/*
 * Part of the Realistic Ore Veins Mod by AlcatrazEscapee
 * Work under Copyright. See the project LICENSE.md for details.
 */

package com.alcatrazescapee.oreveins.world.vein;


import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.math.BlockPos;

@ParametersAreNonnullByDefault
public class SphereVeinType extends SimpleVeinType
{
    public SphereVeinType(JsonObject obj, JsonDeserializationContext context) throws JsonParseException
    {
        super(obj, context);
    }

    @Override
    public float getChanceToGenerate(Vein<?> vein, BlockPos pos)
    {
        final double dx = Math.pow(vein.getPos().getX() - pos.getX(), 2);
        final double dy = Math.pow(vein.getPos().getY() - pos.getY(), 2);
        final double dz = Math.pow(vein.getPos().getZ() - pos.getZ(), 2);

        final float radius = (float) ((dx + dz) / (horizontalSize * horizontalSize * vein.getSize()) +
            dy / (verticalSize * verticalSize * vein.getSize()));
        return 0.005f * density * (1.0f - radius);
    }
}
