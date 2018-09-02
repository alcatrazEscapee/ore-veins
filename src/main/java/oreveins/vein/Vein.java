package oreveins.vein;

import java.util.Random;

import net.minecraft.util.math.BlockPos;

public class Vein
{
    private final VeinType type;
    private final BlockPos pos;
    private final float size;

    Vein(VeinType type, BlockPos pos, Random random)
    {
        this.pos = pos;
        this.type = type;
        this.size = 0.7f + random.nextFloat() * 0.3f;
    }

    public VeinType getType()
    {
        return type;
    }

    public boolean inRange(int x, int z)
    {
        return type.inRange(this, pos.getX() - x, pos.getZ() - z);
    }

    public float getChanceToGenerateAt(BlockPos pos)
    {
        return type.getChanceToGenerate(this, pos);
    }

    BlockPos getPos()
    {
        return pos;
    }

    float getSize()
    {
        return size;
    }
}
