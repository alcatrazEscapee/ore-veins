/*
 *  Part of the Ore Veins Mod by alcatrazEscapee
 *  Work under Copyright. Licensed under the GPL-3.0.
 *  See the project LICENSE.md for more information.
 */

package oreveins.vein;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import com.typesafe.config.Config;
import oreveins.util.ConfigHelper;

import java.util.Random;

@ParametersAreNonnullByDefault
public class VeinTypeCurve extends VeinType
{
    private final float radius;
    private final float angle;

    public VeinTypeCurve(String name, Config config)
    {
        super(name, config);
        this.radius = ConfigHelper.getValue(config, "radius", 5.0f);
        this.angle = ConfigHelper.getValue(config, "angle", 45.0f);
    }

    @Override
    public Vein createVein(BlockPos pos, Random rand)
    {
        return new VeinCurve(this, pos, rand);
    }

    @Override
    boolean inRange(Vein vein, int xOffset, int zOffset)
    {
        return (xOffset < this.horizontalSize * vein.getSize()) && (zOffset < this.horizontalSize * vein.getSize());
    }

    @Override
    float getChanceToGenerate(Vein vein, BlockPos pos)
    {
        VeinCurve veinCurve = (VeinCurve) vein;

        if (!veinCurve.isInitialized())
            veinCurve.initialize(this.horizontalSize, this.verticalSize, this.angle);

        for (VeinCurve.CurveSegment segment : veinCurve.getSegmentList())
        {
            Vec3d blockPos = new Vec3d(pos);
            Vec3d centeredPos = blockPos.subtract(segment.begin);

            // rotate block pos around Y axis
            double yaw = segment.yaw;
            Vec3d posX = new Vec3d(Math.cos(yaw) * centeredPos.x + Math.sin(yaw) * centeredPos.z,
                    centeredPos.y,
                    -Math.sin(yaw) * centeredPos.x + Math.cos(yaw) * centeredPos.z);

            // rotate block pos around Z axis
            double pitch = segment.pitch;
            Vec3d posY = new Vec3d(Math.cos(pitch) * posX.x - Math.sin(pitch) * posX.y,
                    Math.sin(pitch) * posX.x + Math.cos(pitch) * posX.y,
                    posX.z);

            double rad = Math.sqrt(posY.x*posY.x + posY.z*posY.z);
            double length = segment.length;

            if (((posY.y >= 0 && posY.y <= length) || (posY.y < 0 && posY.y >= length)) && rad < this.radius)
            {
                return 0.005f * this.density * (1f - 0.9f * (float)rad / this.radius);
            }
        }

        return 0.0f;
    }
}
