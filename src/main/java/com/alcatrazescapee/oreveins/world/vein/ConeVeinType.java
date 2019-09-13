/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.world.vein;

import java.lang.reflect.Type;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.*;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.BlockPos;

@ParametersAreNonnullByDefault
public class ConeVeinType extends SimpleVeinType
{
    private final float shape;
    private final boolean inverted;

    private ConeVeinType(Builder builder, float shape, boolean inverted)
    {
        super(builder);
        this.shape = shape;
        this.inverted = inverted;
    }

    @Override
    public float getChanceToGenerate(Vein<?> vein, BlockPos pos)
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

    public enum Deserializer implements JsonDeserializer<ConeVeinType>
    {
        INSTANCE;

        @Override
        public ConeVeinType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            JsonObject obj = json.getAsJsonObject();
            Builder builder = Builder.deserialize(obj, context);
            float shape = JSONUtils.getFloat(obj, "shape", 0.5f);
            boolean inverted = JSONUtils.getBoolean(obj, "inverted", false);
            return new ConeVeinType(builder, shape, inverted);
        }
    }
}
