package oreveins;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class VeinTypeCluster extends IVeinType {

    private double sizeModifier = 1.0D;
    private double densityModifier = 1.0D;

    VeinTypeCluster(@Nonnull IBlockState state, int x, int y, int z, double sizeModifier, double densityModifier){
        super(state, new BlockPos(x,y,z));

        this.densityModifier = densityModifier;
        this.sizeModifier = sizeModifier;
    }


    @Override
    public double getChanceToGenerate(BlockPos pos1) {
        double d = pos.getDistance(pos1.getX(), pos1.getY(), pos1.getZ());

        // Simple linear function for now
        return 0.6*Math.max(1.0D - 1.2D*d/WorldGen.MAX_RADIUS, 0.0D);
    }
}
