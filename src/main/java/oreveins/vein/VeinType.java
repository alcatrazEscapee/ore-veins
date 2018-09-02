package oreveins.vein;

import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.LinkedListMultimap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.IForgeRegistryEntry;

import com.typesafe.config.Config;
import oreveins.ConfigHelper;

@ParametersAreNonnullByDefault
public abstract class VeinType extends IForgeRegistryEntry.Impl<VeinType>
{
    public final int count;
    public final int rarity;
    public final int minY;
    public final int maxY;
    public final int horizontalSize;
    public final int verticalSize;
    public final int density;
    public final List<String> biomes;
    public final List<Integer> dims;
    public final boolean dimensionIsWhitelist;
    public final boolean biomesIsWhitelist;
    protected final int horizontalSizeSquared;
    protected final int totalWeight;
    private final List<IBlockState> stoneStates;
    private final LinkedListMultimap<IBlockState, Integer> oreStates;

    VeinType(Config config) throws IllegalArgumentException
    {
        this.stoneStates = ConfigHelper.getBlockStateList(config, "stone");
        this.oreStates = ConfigHelper.getWeightedBlockStateList(config, "ore");
        this.biomes = ConfigHelper.getStringList(config, "biomes");
        this.dims = ConfigHelper.getIntList(config, "dimensions");

        this.count = ConfigHelper.getValue(config, "count", 1);
        this.rarity = ConfigHelper.getValue(config, "rarity", 10);
        this.minY = ConfigHelper.getValue(config, "min_y", 16);
        this.maxY = ConfigHelper.getValue(config, "max_y", 64);
        this.horizontalSize = ConfigHelper.getValue(config, "horizontal_size", 15);
        this.verticalSize = ConfigHelper.getValue(config, "vertical_size", 8);
        this.density = ConfigHelper.getValue(config, "density", 50);
        this.dimensionIsWhitelist = ConfigHelper.getBoolean(config, "dimensions_is_whitelist", true);
        this.biomesIsWhitelist = ConfigHelper.getBoolean(config, "biomes_is_whitelist", true);

        this.horizontalSizeSquared = horizontalSize * horizontalSize;
        this.totalWeight = oreStates.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * @param rand A random to use in generation
     * @return the state to generate at that location
     */
    @Nonnull
    public IBlockState getStateToGenerate(Random rand)
    {
        float r = rand.nextFloat() * totalWeight;
        float countWeight = 0f;
        for (Map.Entry<IBlockState, Integer> entry : oreStates.entries())
        {
            countWeight += entry.getValue().floatValue();
            if (countWeight >= r)
                return entry.getKey();
        }
        throw new RuntimeException("Problem choosing IBlockState from weighted list");
    }

    public boolean canGenerateIn(IBlockState state)
    {
        return stoneStates.contains(state);
    }

    @Nonnull
    public Vein createVein(BlockPos pos, Random rand)
    {
        return new Vein(this, pos, rand);
    }

    /**
     * Use vein.inRange instead of calling this
     *
     * @param vein    The vein instance
     * @param xOffset The offset from the origin
     * @param zOffset The offset from the origin of the vein
     * @return if the vein is in range to generate
     */
    boolean inRange(Vein vein, int xOffset, int zOffset)
    {
        return xOffset * xOffset + zOffset * zOffset < horizontalSizeSquared * vein.getSize();
    }

    /**
     * @param vein The vein instance
     * @param pos  The pos to generate at. Use vein.getPos() - pos to get the relative pos
     * @return the chance to generate: 1.0 = 100%, 0.0 = 0% chance
     */
    abstract float getChanceToGenerate(Vein vein, BlockPos pos);
}
