/*
 * Part of the Realistic Ore Veins Mod by AlcatrazEscapee
 * Work under Copyright. See the project LICENSE.md for details.
 */

package com.alcatrazescapee.oreveins.world.vein;

import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.math.BlockPos;

public class PipeVeinType extends SingleVeinType<Vein<?>>
{
    public PipeVeinType(JsonObject obj, JsonDeserializationContext context) throws JsonParseException
    {
        super(obj, context);
    }

    @Override
    public Vein<?> createVein(int chunkX, int chunkZ, Random random)
    {
        return createDefaultVein(chunkX, chunkZ, random);
    }

    @Override
    public float getChanceToGenerate(Vein<?> vein, BlockPos pos)
    {
        float sizeMod = verticalSize;
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
