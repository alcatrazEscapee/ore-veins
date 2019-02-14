package com.alcatrazescapee.oreveins.api;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A default vein type implementation. Used for veins that don't require any type parameters
 *
 * @author AlcatrazEscapee
 */
@ParametersAreNonnullByDefault
public abstract class DefaultVeinType extends AbstractVeinType<DefaultVein>
{
    @Nonnull
    @Override
    public DefaultVein createVein(int chunkX, int chunkZ, Random rand)
    {
        return new DefaultVein(this, defaultStartPos(chunkX, chunkZ, rand), rand);
    }
}
