/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.world.vein;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import net.minecraft.block.BlockState;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.Dimension;

import com.alcatrazescapee.oreveins.Config;
import com.alcatrazescapee.oreveins.util.IWeightedList;
import com.alcatrazescapee.oreveins.world.rule.BiomeRule;
import com.alcatrazescapee.oreveins.world.rule.DimensionRule;
import com.alcatrazescapee.oreveins.world.rule.IRule;

@ParametersAreNonnullByDefault
public abstract class VeinType<V extends Vein<?>>
{
    protected final int count;
    protected final int rarity;
    protected final int minY;
    protected final int maxY;
    protected final int verticalSize;
    protected final int horizontalSize;
    protected final float density;

    protected final boolean dimensionIsWhitelist;

    private final List<BlockState> stoneStates;
    private final IWeightedList<BlockState> oreStates;

    private final BiomeRule biomes;
    private final DimensionRule dimensions;
    private final List<IRule> rules;
    private final IWeightedList<Indicator> indicator;

    protected VeinType(Builder builder)
    {
        this.count = builder.count;
        this.rarity = builder.rarity;
        this.minY = builder.minY;
        this.maxY = builder.maxY;
        this.verticalSize = builder.verticalSize;
        this.horizontalSize = builder.horizontalSize;
        this.density = builder.density;
        this.dimensionIsWhitelist = builder.dimensionIsWhitelist;
        this.stoneStates = builder.stoneStates;
        this.oreStates = builder.oreStates;
        this.biomes = builder.biomes;
        this.dimensions = builder.dimensions;
        this.rules = builder.rules;
        this.indicator = builder.indicator;
    }

    /**
     * Gets the state to generate at a point.
     * Handled by {@link VeinType} using a weighted list
     *
     * @param rand A random to use in generation
     * @return A block state
     */
    @Nonnull
    public BlockState getStateToGenerate(Random rand)
    {
        return oreStates.get(rand);
    }

    /**
     * Gets all possible ore states spawned by this vein.
     * Used for command vein searching / world stripping
     *
     * @return a collection of block states
     */
    @Nonnull
    public Collection<BlockState> getOreStates()
    {
        return oreStates.values();
    }

    /**
     * Gets an indicator for this vein type
     *
     * @param random A random to use to select an indicator
     * @return An Indicator if it exists, or null if not
     */
    @Nullable
    public Indicator getIndicator(Random random)
    {
        return indicator != null ? indicator.get(random) : null;
    }

    /**
     * If the vein can generate on the previous state
     *
     * @param world The world
     * @param pos   The position to generate at
     * @return if the vein can generate
     */
    public boolean canGenerateAt(IBlockReader world, BlockPos pos)
    {
        BlockState stoneState = world.getBlockState(pos);
        if (stoneStates.contains(stoneState))
        {
            if (rules != null)
            {
                for (IRule rule : rules)
                {
                    if (!rule.test(world, pos))
                    {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Is the vein in range of a vertical column with specific offsets
     * This should be a simple check for optimization purposes
     *
     * @param vein    The vein instance
     * @param xOffset The x offset
     * @param zOffset The y offset
     * @return if the vein can generate any blocks in this column
     */
    public boolean inRange(V vein, int xOffset, int zOffset)
    {
        return xOffset * xOffset + zOffset * zOffset < horizontalSize * horizontalSize * vein.getSize();
    }

    /**
     * Check if the dimension is valid for this vein
     *
     * @param dimension a dimension
     * @return true if the dimension is valid
     */
    public boolean matchesDimension(Dimension dimension)
    {
        return dimensions.test(dimension);
    }

    /**
     * Check if the biome is valid for this vein
     *
     * @param biome a biome
     * @return true if the biome is valid
     */
    public boolean matchesBiome(Biome biome)
    {
        return biomes.test(biome);
    }

    /**
     * Gets the min Y which this vein can spawn at
     *
     * @return a Y position
     */
    public int getMinY()
    {
        return minY;
    }

    /**
     * Gets the max Y which this vein can spawn at
     *
     * @return a Y position
     */
    public int getMaxY()
    {
        return maxY;
    }

    /**
     * Gets the number of rolls for a chunk
     *
     * @return a number in [1...]
     */
    public int getCount()
    {
        return count;
    }

    /**
     * Gets the rarity of this vein in a chunk
     *
     * @return a number in [1...]
     */
    public int getRarity()
    {
        return rarity;
    }

    /**
     * Gets the max chunk radius that this vein needs to check
     *
     * @return a radius in chunks
     */
    public int getChunkRadius()
    {
        return 1 + (horizontalSize >> 4);
    }

    @Override
    public String toString()
    {
        return String.format("[%s: Count: %d, Rarity: %d, Y: %d - %d, Size: %d / %d, Density: %2.2f, Ores: %s, Stones: %s]", VeinManager.INSTANCE.getName(this), count, rarity, minY, maxY, horizontalSize, verticalSize, density, oreStates, stoneStates);
    }

    /**
     * Gets the chance to generate at a specific location
     *
     * @param vein the vein instance
     * @param pos  the position
     * @return a chance: 0 = 0% chance, 1 = 100% chance
     */
    public abstract float getChanceToGenerate(V vein, BlockPos pos);

    /**
     * Creates an instance of a vein
     *
     * @param chunkX The chunkX
     * @param chunkZ The chunkZ
     * @param rand   a random to use in generation
     * @return a new vein instance
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    public V createVein(int chunkX, int chunkZ, Random rand)
    {
        return (V) new Vein<>(this, defaultStartPos(chunkX, chunkZ, rand), rand);
    }

    protected final BlockPos defaultStartPos(int chunkX, int chunkZ, Random rand)
    {
        int spawnRange = maxY - minY, minRange = minY;
        if (Config.SERVER.avoidVeinCutoffs.get())
        {
            if (verticalSize * 2 < spawnRange)
            {
                spawnRange -= verticalSize * 2;
                minRange += verticalSize;
            }
            else
            {
                minRange = minY + (maxY - minY) / 2;
                spawnRange = 1;
            }
        }
        return new BlockPos(chunkX * 16 + rand.nextInt(16), minRange + rand.nextInt(spawnRange), chunkZ * 16 + rand.nextInt(16));
    }

    public static class Builder
    {
        @Nonnull
        public static Builder deserialize(JsonObject json, JsonDeserializationContext context) throws JsonParseException
        {
            Builder builder = new Builder();
            builder.count = JSONUtils.getInt(json, "count", 1);
            if (builder.count <= 0)
            {
                throw new JsonParseException("Count must be > 0.");
            }
            builder.rarity = JSONUtils.getInt(json, "rarity", 10);
            if (builder.rarity <= 0)
            {
                throw new JsonParseException("Count must be > 0.");
            }
            builder.minY = JSONUtils.getInt(json, "min_y", 16);
            builder.maxY = JSONUtils.getInt(json, "max_y", 64);
            if (builder.minY < 0 || builder.maxY > 256 || builder.minY > builder.maxY)
            {
                throw new JsonParseException("Min Y and Max Y must be within [0, 256], and Min Y must be <= Max Y.");
            }
            builder.verticalSize = JSONUtils.getInt(json, "vertical_size", 8);
            if (builder.verticalSize <= 0)
            {
                throw new JsonParseException("Vertical Size must be > 0.");
            }
            builder.horizontalSize = JSONUtils.getInt(json, "horizontal_size", 15);
            if (builder.horizontalSize <= 0)
            {
                throw new JsonParseException("Horizontal Size must be > 0.");
            }
            builder.density = JSONUtils.getInt(json, "density", 20);
            if (builder.density <= 0)
            {
                throw new JsonParseException("Density must be > 0.");
            }
            builder.dimensionIsWhitelist = JSONUtils.getBoolean(json, "dimensions_is_whitelist", true);
            builder.stoneStates = context.deserialize(json.get("stone"), new TypeToken<List<BlockState>>() {}.getType());
            if (builder.stoneStates.isEmpty())
            {
                throw new JsonParseException("Stone States cannot be empty.");
            }
            builder.oreStates = context.deserialize(json.get("ore"), new TypeToken<IWeightedList<BlockState>>() {}.getType());
            if (builder.oreStates.isEmpty())
            {
                throw new JsonParseException("Ore States cannot be empty.");
            }
            builder.biomes = json.has("biomes") ? context.deserialize(json.get("biomes"), BiomeRule.class) : BiomeRule.DEFAULT;
            builder.dimensions = json.has("dimensions") ? context.deserialize(json.get("dimensions"), DimensionRule.class) : DimensionRule.DEFAULT;
            builder.rules = json.has("rules") ? context.deserialize(json.get("rules"), new TypeToken<List<IRule>>() {}.getType()) : Collections.emptyList();
            builder.indicator = json.has("indicator") ? context.deserialize(json.get("indicator"), new TypeToken<IWeightedList<Indicator>>() {}.getType()) : IWeightedList.empty();
            return builder;
        }

        private int count;
        private int rarity;
        private int minY;
        private int maxY;
        private int verticalSize;
        private int horizontalSize;
        private float density;
        private boolean dimensionIsWhitelist;
        private List<BlockState> stoneStates;
        private IWeightedList<BlockState> oreStates;
        private BiomeRule biomes;
        private DimensionRule dimensions;
        private List<IRule> rules;
        private IWeightedList<Indicator> indicator = null;
    }
}
