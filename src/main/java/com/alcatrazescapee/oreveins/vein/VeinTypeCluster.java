/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.vein;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.math.BlockPos;

import com.alcatrazescapee.oreveins.api.AbstractVein;
import com.alcatrazescapee.oreveins.api.AbstractVeinType;

@SuppressWarnings({"unused", "WeakerAccess"})
@ParametersAreNonnullByDefault
public class VeinTypeCluster extends AbstractVeinType<VeinTypeCluster.VeinCluster>
{
    int clusters = 3;

    @Override
    public float getChanceToGenerate(VeinCluster vein, BlockPos pos)
    {
        float shortestRadius = -1;
        for (Cluster c : vein.spawnPoints)
        {
            final double dx = Math.pow(c.pos.getX() - pos.getX(), 2);
            final double dy = Math.pow(c.pos.getY() - pos.getY(), 2);
            final double dz = Math.pow(c.pos.getZ() - pos.getZ(), 2);

            final float radius = (float) ((dx + dz) / (horizontalSize * horizontalSize * vein.getSize() * c.size) +
                    dy / (verticalSize * verticalSize * vein.getSize() * c.size));

            if (shortestRadius == -1 || radius < shortestRadius) shortestRadius = radius;
        }
        return 0.005f * density * (1.0f - shortestRadius);
    }

    @Nonnull
    @Override
    public VeinCluster createVein(int chunkX, int chunkZ, Random rand)
    {
        return new VeinCluster(this, defaultStartPos(chunkX, chunkZ, rand), rand);
    }

    @Override
    public boolean inRange(VeinCluster vein, int xOffset, int zOffset)
    {
        return xOffset * xOffset + zOffset * zOffset < horizontalSize * horizontalSize * vein.getSize();
    }

    static class VeinCluster extends AbstractVein<VeinTypeCluster>
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

        @Override
        public boolean inRange(int x, int z)
        {
            return getType().inRange(this, getPos().getX() - x, getPos().getZ() - z);
        }

        @Override
        public double getChanceToGenerate(@Nonnull BlockPos pos)
        {
            return getType().getChanceToGenerate(this, pos);
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
