package oreveins.vein;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.math.BlockPos;

import com.typesafe.config.Config;

@ParametersAreNonnullByDefault
public class VeinTypeSphere extends VeinType
{

    public VeinTypeSphere(Config config)
    {
        super(config);
    }

    @Override
    public float getChanceToGenerate(Vein vein, BlockPos pos)
    {
        final double dx = Math.pow(vein.getPos().getX() - pos.getX(), 2);
        final double dy = Math.pow(vein.getPos().getY() - pos.getY(), 2);
        final double dz = Math.pow(vein.getPos().getZ() - pos.getZ(), 2);

        final float radius = (float) ((dx + dz) / (vein.getType().horizontalSizeSquared * vein.getSize()) +
                dy / (vein.getType().verticalSize * vein.getType().verticalSize * vein.getSize()));

        return 0.005f * vein.getType().density * (1.0f - radius);
    }

}
