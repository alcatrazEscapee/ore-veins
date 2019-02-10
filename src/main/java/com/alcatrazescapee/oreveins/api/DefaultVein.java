package com.alcatrazescapee.oreveins.api;

import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.math.BlockPos;

@SuppressWarnings("WeakerAccess")
@ParametersAreNonnullByDefault
public class DefaultVein extends AbstractVein<DefaultVeinType>
{
    public DefaultVein(DefaultVeinType type, BlockPos pos, Random rand)
    {
        super(type, pos, rand);
    }

    @Override
    public boolean inRange(int x, int z)
    {
        return getType().inRange(this, getPos().getX() - x, getPos().getZ() - z);
    }

    @Override
    public double getChanceToGenerate(BlockPos pos)
    {
        return getType().getChanceToGenerate(this, pos);
    }
}
