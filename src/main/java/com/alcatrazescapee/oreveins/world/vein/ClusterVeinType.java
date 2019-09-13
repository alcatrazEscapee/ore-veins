/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.world.vein;

import java.lang.reflect.Type;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.*;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.BlockPos;

@ParametersAreNonnullByDefault
public class ClusterVeinType extends VeinType<ClusterVeinType.VeinCluster>
{
    private final int clusters;

    private ClusterVeinType(Builder builder, int clusters)
    {
        super(builder);
        this.clusters = clusters;
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

    @Nonnull
    @Override
    public VeinCluster createVein(int chunkX, int chunkZ, Random rand)
    {
        return new VeinCluster(this, defaultStartPos(chunkX, chunkZ, rand), rand);
    }

    public enum Deserializer implements JsonDeserializer<ClusterVeinType>
    {
        INSTANCE;

        @Override
        @Nonnull
        public ClusterVeinType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            JsonObject obj = json.getAsJsonObject();
            Builder builder = Builder.deserialize(obj, context);
            int clusters = JSONUtils.getInt(obj, "clusters", 3);
            if (clusters <= 0)
            {
                throw new JsonParseException("Clusters must be > 0");
            }
            return new ClusterVeinType(builder, clusters);
        }
    }

    static class VeinCluster extends Vein<ClusterVeinType>
    {
        private final Cluster[] spawnPoints;

        VeinCluster(ClusterVeinType type, BlockPos pos, Random rand)
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
