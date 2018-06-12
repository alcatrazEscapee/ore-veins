package oreveins;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class VeinTypeCluster extends IVeinType {

    private final double densityModifier;
    private final double verticalModifier;
    private final double horizontalModifier;

    VeinTypeCluster(@Nonnull IBlockState state, int x, int y, int z, double verticalModifier, double horizontalModifier, double densityModifier){
        super(state, new BlockPos(x,y,z));

        this.densityModifier = densityModifier;
        this.horizontalModifier = horizontalModifier;
        this.verticalModifier = verticalModifier;
    }


    @Override
    public double getChanceToGenerate(BlockPos pos1) {
        final double dx = Math.pow(pos.getX() - pos1.getX(),2);
        final double dy = Math.pow(pos.getY() - pos1.getY(),2);
        final double dz = Math.pow(pos.getZ() - pos1.getZ(),2);

        final double radius = (dx + dz)/Math.pow(horizontalModifier,2) + dy/Math.pow(verticalModifier,2);

        return densityModifier*(1.0D - radius);
    }
}
