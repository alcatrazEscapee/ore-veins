/*
 Part of the Ore Veins Mod by alcatrazEscapee
 Work under Copyright. Licensed under the GPL-3.0.
 See the project LICENSE.md for more information.
 */

package oreveins.world.vein;

import com.typesafe.config.Config;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import oreveins.api.Ore;
import oreveins.api.OreVeinsApi;
import oreveins.api.Vein;
import oreveins.world.ore.OreCluster;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class VeinCluster extends Vein {

    private Cluster[] spawnPoints;
    private BlockPos startPos;

    public VeinCluster() {
    }

    @SuppressWarnings("unused")
    public VeinCluster(Ore ore, BlockPos pos, Random rand) throws IllegalArgumentException {
        super(ore, pos, rand);
        if (!(ore instanceof OreCluster)) throw new IllegalArgumentException("Incorrect ore type passed in");
        this.startPos = pos;

        int clusters = 2 + rand.nextInt(4);
        spawnPoints = new Cluster[clusters];
        spawnPoints[0] = new Cluster(pos, 0.6 + 0.5 * rand.nextDouble());
        for (int i = 1; i < clusters; i++) {
            final BlockPos clusterPos = pos.add(
                    1.5 * ore.horizontalSize * (0.5 - rand.nextDouble()),
                    1.5 * ore.verticalSize * (0.5 - rand.nextDouble()),
                    1.5 * ore.horizontalSize * (0.5 - rand.nextDouble())
            );
            spawnPoints[i] = new Cluster(clusterPos, 0.3 + 0.5 * rand.nextDouble());
        }
    }

    @Override
    public double getChanceToGenerate(BlockPos pos) {
        double shortestRadius = -1;

        for (Cluster c : spawnPoints) {
            final double dx = Math.pow(c.pos.getX() - pos.getX(), 2);
            final double dy = Math.pow(c.pos.getY() - pos.getY(), 2);
            final double dz = Math.pow(c.pos.getZ() - pos.getZ(), 2);

            final double radius = (dx + dz) / Math.pow(c.size * ore.horizontalSize, 2) + dy / Math.pow(c.size * ore.horizontalSize, 2);

            if (shortestRadius == -1 || radius < shortestRadius) shortestRadius = radius;
        }
        return 0.002 * ((OreCluster) ore).density * (1.0 - shortestRadius);
    }

    @Override
    public IBlockState getStateToGenerate(BlockPos pos) {
        return OreVeinsApi.getWeightedOre(((OreCluster) ore).oreStates);
    }

    @Override
    public boolean inRange(int xPos, int zPos) {
        return Math.pow(xPos - startPos.getX(), 2) + Math.pow(zPos - startPos.getZ(), 2) <= ore.horizontalSize * ore.horizontalSize;
    }

    @Override
    public Ore createOre(Config config) {
        return new OreCluster(config);
    }

    final class Cluster {
        final BlockPos pos;
        final double size;

        Cluster(BlockPos pos, double size) {
            this.pos = pos;
            this.size = size;
        }

    }
}
