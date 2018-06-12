package oreveins;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public abstract class IVeinType {

    // Vein Types
    // Cluster: one big spherical cluster (modifiers for density, horizontal and vertical size)
    // Vertical Column: vertical column. Nuff said
    // Scattered Cluster: a collection of smaller spherical clusters
    // P2P Vein / Linear Vein: a line between two points.

    final BlockPos pos;
    private final IBlockState state;

    IVeinType(@Nonnull IBlockState state, @Nonnull BlockPos pos){
        this.pos = pos;
        this.state = state;
    }

    protected final boolean inRange(BlockPos pos1){
        return Math.pow(pos1.getX() - this.pos.getX(),2) + Math.pow(pos1.getZ() - this.pos.getZ(),2) <= WorldGen.MAX_RADIUS_SQUARED;
    }
    protected int getLowestY(){
        return pos.getY() - WorldGen.MAX_RADIUS / 2;
    }
    protected int getHighestY(){
        return pos.getY() + WorldGen.MAX_RADIUS / 2;
    }

    @Nonnull
    protected BlockPos getPos(){
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
