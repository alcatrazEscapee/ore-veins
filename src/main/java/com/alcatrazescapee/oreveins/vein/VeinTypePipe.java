package com.alcatrazescapee.oreveins.vein;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.math.BlockPos;

import com.alcatrazescapee.oreveins.api.DefaultVein;
import com.alcatrazescapee.oreveins.api.DefaultVeinType;

@SuppressWarnings({"unused"})
@ParametersAreNonnullByDefault
public class VeinTypePipe extends DefaultVeinType
{
    @Override
    public float getChanceToGenerate(DefaultVein vein, BlockPos pos)
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
}
