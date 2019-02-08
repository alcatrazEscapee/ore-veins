/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.vein;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

import com.alcatrazescapee.oreveins.util.ConfigHelper;
import com.typesafe.config.Config;
import gnu.trove.map.hash.TObjectIntHashMap;

@ParametersAreNonnullByDefault
public abstract class VeinType
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
    protected final int verticalSizeSquared;
    protected final int totalWeight;

    private final List<IBlockState> stoneStates;
    private TObjectIntHashMap<IBlockState> oreStates;
    private final Indicator indicator;
    private final String name;

    VeinType(String name, Config config) throws IllegalArgumentException
    {
        this.stoneStates = ConfigHelper.getBlockStateList(config, "stone");
        this.oreStates = ConfigHelper.getWeightedBlockStateList(config, "ore");
        this.biomes = ConfigHelper.getStringList(config, "biomes");
        this.dims = ConfigHelper.getIntList(config, "dimensions");
        this.indicator = config.hasPath("indicator") ? new Indicator(config.getConfig("indicator")) : null;

        this.count = ConfigHelper.getValue(config, "count", 1);
        this.rarity = ConfigHelper.getValue(config, "rarity", 10);
        this.minY = ConfigHelper.getValue(config, "min_y", 16);
        this.maxY = ConfigHelper.getValue(config, "max_y", 64);

        if (this.minY > this.maxY)
            throw new IllegalArgumentException("min_y is not allowed to be greater than max_y");

        this.horizontalSize = ConfigHelper.getValue(config, "horizontal_size", 15);
        this.verticalSize = ConfigHelper.getValue(config, "vertical_size", 8);
        this.density = ConfigHelper.getValue(config, "density", 50);
        this.dimensionIsWhitelist = ConfigHelper.getBoolean(config, "dimensions_is_whitelist", true);
        this.biomesIsWhitelist = ConfigHelper.getBoolean(config, "biomes_is_whitelist", true);

        this.horizontalSizeSquared = horizontalSize * horizontalSize;
        this.verticalSizeSquared = verticalSize * verticalSize;
        this.totalWeight = Arrays.stream(oreStates.values()).sum();

        this.name = name;
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
        for (IBlockState state : oreStates.keySet())
        {
            countWeight += (float) oreStates.get(state);
            if (countWeight >= r)
                return state;
        }
        throw new RuntimeException("Problem choosing IBlockState from weighted list");
    }

    public boolean canGenerateIn(IBlockState state)
    {
        return stoneStates.contains(state);
    }

    public Vein createVein(int chunkX, int chunkZ, Random rand)
    {
        return new Vein(this, defaultStartPos(chunkX, chunkZ, rand), rand);
    }

    final BlockPos defaultStartPos(int chunkX, int chunkZ, Random rand)
    {
        return new BlockPos(
                chunkX * 16 + rand.nextInt(16),
                this.minY + rand.nextInt(this.maxY - this.minY),
                chunkZ * 16 + rand.nextInt(16)
        );
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

    public Set<IBlockState> getOreStates()
    {
        return oreStates.keySet();
    }

    @Nullable
    public Indicator getIndicator()
    {
        return indicator;
    }

    @Override
    public String toString()
    {
        return String.format("'%s':  rarity: %d, count: %d, y-range: %d - %d, size: %d / %d, density: %d", name, rarity, count, minY, maxY, horizontalSize, verticalSize, density);
    }

    @Nonnull
    public String getRegistryName()
    {
        return name;
    }
}