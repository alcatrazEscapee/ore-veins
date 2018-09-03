/*
 *  Part of the Ore Veins Mod by alcatrazEscapee
 *  Work under Copyright. Licensed under the GPL-3.0.
 *  See the project LICENSE.md for more information.
 */

package oreveins.vein;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.math.BlockPos;

import com.typesafe.config.Config;
import oreveins.util.ConfigHelper;

@ParametersAreNonnullByDefault
public class VeinTypeCluster extends VeinType
{
    private final int clusters;

    public VeinTypeCluster(Config config)
    {
        super(config);
        this.clusters = ConfigHelper.getValue(config, "clusters", 3);
    }

    @Override
    @Nonnull
    public Vein createVein(BlockPos pos, Random rand)
    {
        return new VeinCluster(this, pos, rand);
    }

    @Override
    public float getChanceToGenerate(Vein vein, BlockPos pos)
    {
        float shortestRadius = -1;
        for (Cluster c : ((VeinCluster) vein).spawnPoints)
        {
            final double dx = Math.pow(c.pos.getX() - pos.getX(), 2);
            final double dy = Math.pow(c.pos.getY() - pos.getY(), 2);
            final double dz = Math.pow(c.pos.getZ() - pos.getZ(), 2);

            final float radius = (float) ((dx + dz) / (this.horizontalSizeSquared * vein.getSize() * c.size) +
                    dy / (this.verticalSizeSquared * vein.getSize() * c.size));

            if (shortestRadius == -1 || radius < shortestRadius) shortestRadius = radius;
        }
        return 0.005f * this.density * (1.0f - shortestRadius);
    }

    private static class VeinCluster extends Vein
    {
        private final Cluster[] spawnPoints;

        VeinCluster(VeinTypeCluster type, BlockPos pos, Random rand)
        {
            super(type, pos, rand);

            int clusters = (int) (type.clusters * (0.5f + rand.nextFloat()));
            spawnPoints = new Cluster[clusters];
            spawnPoints[0] = new Cluster(pos, 0.6f + 0.2f * rand.nextFloat());
            for (int i = 1; i < clusters; i++)
            {
                final BlockPos clusterPos = pos.add(
                        type.horizontalSize * (0.3f - 0.6f * rand.nextFloat()),
                        type.verticalSize * (0.3f - 0.6f * rand.nextFloat()),
                        type.horizontalSize * (0.3f - 0.6f * rand.nextFloat())
                );
                spawnPoints[i] = new Cluster(clusterPos, 0.2f + 0.5f * rand.nextFloat());
            }
        }
    }

    private static class Cluster
    {
        private final BlockPos pos;
        private final float size;

        private Cluster(BlockPos pos, float size)
        {
            this.pos = pos;
            this.size = size;
        }

    }
}
