package oreveins;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public class VeinTypeColumn extends IVeinType{

    private final int height;
    private final int radiusTop;
    private final int radiusBot;
    private final double densityModifier;
    private final double densityHeightModifier;

    VeinTypeColumn(IBlockState state, int x, int yMax, int z, int height, int radiusTop, int radiusBot, double densityModifier, double densityHeightModifier){
        super(state, new BlockPos(x, yMax, z));

        this.height = height;
        this.radiusTop = radiusTop;
        this.radiusBot = radiusBot;
        this.densityModifier = densityModifier;
        this.densityHeightModifier = densityHeightModifier;
    }

    @Override
    double getChanceToGenerate(BlockPos pos1) {

        final double dx = Math.pow(this.pos.getX() - pos1.getX(), 2);
        final double dz = Math.pow(this.pos.getZ() - pos1.getZ(), 2);
        final double radius = radiusTop + (radiusBot - radiusTop)*(this.pos.getY() - pos1.getY());

        final double heightModifier = ((this.pos.getY() - height/2) - pos1.getY())/(height / 2);
        final double density = densityModifier + heightModifier*densityHeightModifier;

        return density*(1 - (dx + dz)/Math.pow(radius,2));
    }
}
