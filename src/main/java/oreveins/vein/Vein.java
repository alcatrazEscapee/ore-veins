/*
 *  Part of the Ore Veins Mod by alcatrazEscapee
 *  Work under Copyright. Licensed under the GPL-3.0.
 *  See the project LICENSE.md for more information.
 */

package oreveins.vein;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.math.BlockPos;

@ParametersAreNonnullByDefault
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

    @Nonnull
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

    @Nonnull
    BlockPos getPos()
    {
        return pos;
    }

    float getSize()
    {
        return size;
    }
}
