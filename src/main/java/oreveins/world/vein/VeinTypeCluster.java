package oreveins.world.vein;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import oreveins.GenHandler;
import oreveins.world.WorldGenVeins;

public class VeinTypeCluster {

    public final GenHandler.Ore ore;
    private final BlockPos pos;
    private final Cluster[] spawnPoints;

    public VeinTypeCluster(GenHandler.Ore ore, BlockPos startPos, Random rand){

        this.ore = ore;
        this.pos = startPos;

        int clusters = 2 + rand.nextInt(4);
        spawnPoints = new Cluster[clusters];
        spawnPoints[0] = new Cluster(pos, 0.6 + 0.5 * rand.nextDouble());
        for (int i = 1; i < clusters; i++)
        {
            final BlockPos clusterPos = pos.add(
                    1.5 * ore.horizontalSize * (0.5 - rand.nextDouble()),
                    1.5 * ore.verticalSize * (0.5 - rand.nextDouble()),
                    1.5 * ore.horizontalSize * (0.5 - rand.nextDouble())
            );
            spawnPoints[i] = new Cluster(clusterPos, 0.3 + 0.5 * rand.nextDouble());
        }
    }

    public double getChanceToGenerate(BlockPos pos1)
    {
        double shortestRadius = -1;

        for (Cluster c : spawnPoints)
        {
            final double dx = Math.pow(c.pos.getX() - pos1.getX(), 2);
            final double dy = Math.pow(c.pos.getY() - pos1.getY(), 2);
            final double dz = Math.pow(c.pos.getZ() - pos1.getZ(), 2);

            final double radius = (dx + dz) / Math.pow(c.size * ore.horizontalSize, 2) + dy / Math.pow(c.size * ore.horizontalSize, 2);

            if (shortestRadius == -1 || radius < shortestRadius) shortestRadius = radius;
        }
        return 0.002 * ore.density * (1.0 - shortestRadius);
    }

    public final boolean inRange(BlockPos pos1){
        return Math.pow(pos1.getX() - this.pos.getX(),2) + Math.pow(pos1.getZ() - this.pos.getZ(),2) <= this.ore.horizontalSize * this.ore.horizontalSize;
    }

    final class Cluster
    {
        final BlockPos pos;
        final double size;

        Cluster(BlockPos pos, double size)
        {
            this.pos = pos;
            this.size = size;
        }

    }
}
