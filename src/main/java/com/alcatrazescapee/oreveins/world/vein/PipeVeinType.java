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
public class PipeVeinType extends SimpleVeinType
{
    private PipeVeinType(Builder builder)
    {
        super(builder);
    }

    @Override
    public float getChanceToGenerate(Vein<?> vein, BlockPos pos)
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

    public enum Deserializer implements JsonDeserializer<PipeVeinType>
    {
        INSTANCE;

        @Override
        public PipeVeinType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            return new PipeVeinType(Builder.deserialize(json.getAsJsonObject(), context));
        }
    }
}
