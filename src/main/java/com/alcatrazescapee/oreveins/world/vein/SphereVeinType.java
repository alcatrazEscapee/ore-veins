/*
 * Part of the Realistic Ore Veins Mod by AlcatrazEscapee
 * Work under Copyright. See the project LICENSE.md for details.
 */

package com.alcatrazescapee.oreveins.world.vein;

import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.BlockPos;

public class SphereVeinType extends SingleVeinType<Vein<?>>
{
    private final boolean uniform;

    public SphereVeinType(JsonObject obj, JsonDeserializationContext context) throws JsonParseException
    {
        super(obj, context);

        uniform = JSONUtils.getBoolean(obj, "uniform", false);
    }

    @Override
    public Vein<?> createVein(int chunkX, int chunkZ, Random random)
    {
        return createDefaultVein(chunkX, chunkZ, random);
    }

    @Override
    public float getChanceToGenerate(Vein<?> vein, BlockPos pos)
    {
        float dx = (vein.getPos().getX() - pos.getX()) * (vein.getPos().getX() - pos.getX());
        float dy = (vein.getPos().getY() - pos.getY()) * (vein.getPos().getY() - pos.getY());
        float dz = (vein.getPos().getZ() - pos.getZ()) * (vein.getPos().getZ() - pos.getZ());

        float radius = ((dx + dz) / (horizontalSize * horizontalSize) + dy / (verticalSize * verticalSize));
        if (uniform && radius < 1)
        {
            radius = 0;
        }
        return 0.005f * density * (1.0f - radius);
    }
}
