package oreveins.vein;

import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;

import com.typesafe.config.Config;
import oreveins.util.ConfigHelper;

public class Indicator
{
    public final int maxDepth;
    public final float chance;

    private final List<IBlockState> states;

    Indicator(Config config) throws IllegalArgumentException
    {
        this.states = ConfigHelper.getBlockStateList(config, "blocks");
        this.maxDepth = ConfigHelper.getValue(config, "max_depth", 32);
        this.chance = 1f / (float) ConfigHelper.getValue(config, "rarity", 10);
    }

    @Nonnull
    public IBlockState getStateToGenerate(Random random)
    {
        return states.get(random.nextInt(states.size()));
    }
}
