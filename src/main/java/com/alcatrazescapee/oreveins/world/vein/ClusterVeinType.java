/*
 * Part of the Realistic Ore Veins Mod by AlcatrazEscapee
 * Work under Copyright. See the project LICENSE.md for details.
 */

package com.alcatrazescapee.oreveins.world.vein;

import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.BlockPos;

import static com.alcatrazescapee.oreveins.world.vein.ClusterVeinType.VeinCluster;

public class ClusterVeinType extends SingleVeinType<VeinCluster>
{
    private final int clusters;

    public ClusterVeinType(JsonObject obj, JsonDeserializationContext context) throws JsonParseException
    {
        super(obj, context);
        clusters = JSONUtils.getInt(obj, "clusters", 3);
        if (clusters <= 0)
        {
            throw new JsonParseException("Clusters must be > 0. If you set clusters=0 you should just use the sphere vein.");
        }
    }

    @Override
    public boolean inRange(VeinCluster vein, int xOffset, int zOffset)
    {
        return xOffset * xOffset + zOffset * zOffset < horizontalSize * horizontalSize * vein.getSize();
    }

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

    @Override
    public VeinCluster createVein(int chunkX, int chunkZ, Random rand)
    {
        return new VeinCluster(this, defaultStartPos(chunkX, chunkZ, rand), rand);
    }

    static class VeinCluster extends Vein<ClusterVeinType>
    {
        private final Cluster[] spawnPoints;

        private VeinCluster(ClusterVeinType type, BlockPos pos, Random rand)
        {
            super(type, pos, rand);

            int clusters = 1 + type.clusters; // main cluster + smaller outside ones
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
        public double getChanceToGenerate(BlockPos pos)
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
