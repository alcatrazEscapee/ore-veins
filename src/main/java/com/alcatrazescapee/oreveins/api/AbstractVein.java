package com.alcatrazescapee.oreveins.api;

import java.util.Random;
import javax.annotation.Nonnull;

import net.minecraft.util.math.BlockPos;

import com.alcatrazescapee.oreveins.vein.VeinRegistry;

public abstract class AbstractVein<T extends IVeinType<?>> implements IVein<T>
{
    private final T type;
    private final BlockPos pos;
    private final double size;

    public AbstractVein(T type, BlockPos pos, double size)
    {
        this.pos = pos;
        this.type = type;
        this.size = size;
    }

    public AbstractVein(T type, BlockPos pos, Random random)
    {
        this(type, pos, 0.7f + random.nextFloat() * 0.3f);
    }

    @Nonnull
    @Override
    public BlockPos getPos()
    {
        return pos;
    }

    @Override
    public T getType()
    {
        return type;
    }

    public double getSize()
    {
        return size;
    }

    @Override
    public String toString()
    {
        return String.format("Vein: %s, Pos: %s", VeinRegistry.getName(type), pos);
    }
}
