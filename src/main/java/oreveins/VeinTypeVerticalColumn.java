package oreveins;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public class VeinTypeVerticalColumn extends IVeinType{

    VeinTypeVerticalColumn(IBlockState state, int x, int z){
        super(state, new BlockPos(x, 10, z));
    }

    @Override
    double getChanceToGenerate(BlockPos pos1) {
        // Example of long vertical vein
        // More common at lower y levels
        double d = pos.getDistance(pos1.getX(), pos.getY(), pos1.getZ());
        d += pos.getY()*0.8d;

        return 0.7*Math.max(1.0D - 4.0D*d/ WorldGen.MAX_RADIUS, 0.0D);
    }
}
