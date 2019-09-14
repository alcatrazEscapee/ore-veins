/*
 * Part of the Realistic Ore Veins Mod by AlcatrazEscapee
 * Work under Copyright. See the project LICENSE.md for details.
 */

package com.alcatrazescapee.oreveins.world.vein;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.math.BlockPos;

/**
 * Used for veins that don't require a custom vein implementation
 *
 * @author AlcatrazEscapee
 */
@ParametersAreNonnullByDefault
public abstract class SimpleVeinType extends VeinType<Vein<?>>
{
    protected SimpleVeinType(JsonObject obj, JsonDeserializationContext context) throws JsonParseException
    {
        super(obj, context);
    }

    @Nonnull
    @Override
    public Vein<SimpleVeinType> createVein(int chunkX, int chunkZ, Random rand)
    {
        BlockPos pos = defaultStartPos(chunkX, chunkZ, rand);
        return new Vein<>(this, pos, rand);
    }
}
