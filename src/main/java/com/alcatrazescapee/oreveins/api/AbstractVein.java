package com.alcatrazescapee.oreveins.api;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.alcatrazescapee.oreveins.vein.VeinRegistry;

@ParametersAreNonnullByDefault
public abstract class AbstractVein<T extends IVeinType<?>> implements IVein<T>
{
    protected final T type;
    protected final BlockPos pos;
    protected final float size;

    public AbstractVein(T type, BlockPos pos, float size)
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

    public float getSize()
    {
        return size;
    }

    @Override
    public String toString()
    {
        return String.format("Vein: %s, Pos: %s", VeinRegistry.getName(type), pos);
    }

    @Override
    public boolean canGenerateAt(World world, BlockPos pos)
    {
        return getType().canGenerateAt(world, pos);
    }
}
