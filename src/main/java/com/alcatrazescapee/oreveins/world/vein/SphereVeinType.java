/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.world.vein;


import java.lang.reflect.Type;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.util.math.BlockPos;

@ParametersAreNonnullByDefault
public class SphereVeinType extends SimpleVeinType
{
    private SphereVeinType(Builder builder)
    {
        super(builder);
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

    public enum Deserializer implements JsonDeserializer<SphereVeinType>
    {
        INSTANCE;

        @Override
        public SphereVeinType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            return new SphereVeinType(Builder.deserialize(json.getAsJsonObject(), context));
        }
    }
}
