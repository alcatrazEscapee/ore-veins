package oreveins;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public abstract class IVeinType {

    final BlockPos pos;
    private final IBlockState state;

    IVeinType(@Nonnull IBlockState state, @Nonnull BlockPos pos){
        this.pos = pos;
        this.state = state;
    }

    protected boolean inRange(BlockPos pos){
        return this.pos.getDistance(pos.getX(), pos.getY(), pos.getZ()) <= WorldGen.MAX_RADIUS;
    }

    @Nonnull
    protected BlockPos getStartPos(){
        return pos;
    }

    @Nonnull
    protected IBlockState getBlockState(){
        return state;
    }

    // This is the "heat map" style function for vein generation
    // It controls how veins generate. (The result is compared to a Random.nextDouble())
    // Value of 1 = Always generate at this position.
    // Value of 0 = Never generate at this position.
    // Interpolate between.
    // Think of this as a 3-d heatmap, with larger values representing higher density areas
    abstract double getChanceToGenerate(BlockPos pos);
}
