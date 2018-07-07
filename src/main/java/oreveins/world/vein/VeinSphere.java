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

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VeinSphere extends Vein {

    private BlockPos startPos;
    private double size;

    public VeinSphere() {
    }

    @SuppressWarnings("unused")
    public VeinSphere(Ore ore, BlockPos pos, Random rand) throws IllegalArgumentException {
        super(ore, pos, rand);
        if (!(ore instanceof OreCluster)) throw new IllegalArgumentException("Incorrect ore type passed in");
        this.startPos = pos;
        this.size = rand.nextDouble() * 0.3 + 0.7;
    }

    @Override
    public Ore createOre(Config config) {
        return new OreCluster(config);
    }

    @Override
    public IBlockState getStateToGenerate(BlockPos pos) {
        return OreVeinsApi.getWeightedOre(((OreCluster) ore).oreStates);
    }

    @Override
    public double getChanceToGenerate(BlockPos pos) {

        final double dx = Math.pow(startPos.getX() - pos.getX(), 2);
        final double dy = Math.pow(startPos.getY() - pos.getY(), 2);
        final double dz = Math.pow(startPos.getZ() - pos.getZ(), 2);

        final double radius = (dx + dz) / Math.pow(ore.horizontalSize * size, 2) + dy / Math.pow(ore.horizontalSize * size, 2);

        return 0.005 * ((OreCluster) ore).density * (1.0 - radius);
    }

    @Override
    public boolean inRange(int xPos, int zPos) {
        return Math.pow(xPos - startPos.getX(), 2) + Math.pow(zPos - startPos.getZ(), 2) <= ore.horizontalSize * ore.horizontalSize;
    }

}
