package com.alcatrazescapee.oreveins.api;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class DefaultVeinType extends AbstractVeinType<DefaultVein>
{
    @Nonnull
    @Override
    protected DefaultVein createVein(int chunkX, int chunkZ, Random rand)
    {
        return new DefaultVein(this, defaultStartPos(chunkX, chunkZ, rand), rand);
    }
}
