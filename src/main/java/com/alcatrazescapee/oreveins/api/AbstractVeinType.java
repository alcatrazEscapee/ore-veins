/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.api;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.annotations.SerializedName;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.Dimension;
import net.minecraftforge.common.BiomeDictionary;

import com.alcatrazescapee.oreveins.util.IWeightedList;
import com.alcatrazescapee.oreveins.vein.Indicator;
import com.alcatrazescapee.oreveins.vein.VeinRegistry;

@SuppressWarnings({"unused", "WeakerAccess"})
@ParametersAreNonnullByDefault
public abstract class AbstractVeinType<V extends AbstractVein<?>> implements IVeinType<V>
{
    protected int count = 1;
    protected int rarity = 10;
    @SerializedName("min_y")
    protected int minY = 16;
    @SerializedName("max_y")
    protected int maxY = 64;
    @SerializedName("vertical_size")
    protected int verticalSize = 8;
    @SerializedName("horizontal_size")
    protected int horizontalSize = 15;
    protected float density = 20;

    @SerializedName("dimensions_is_whitelist")
    protected boolean dimensionIsWhitelist = true;
    @SerializedName("biomes_is_whitelist")
    protected boolean biomesIsWhitelist = true;

    @SerializedName("stone")
    private List<IBlockState> stoneStates = null;
    @SerializedName("ore")
    private IWeightedList<IBlockState> oreStates = null;

    private List<String> biomes = null;
    private List<String> dimensions = null;
    private Indicator indicator = null;

    @Nonnull
    @Override
    public IBlockState getStateToGenerate(Random rand)
    {
        return oreStates.get(rand);
    }

    @Nonnull
    @Override
    public Collection<IBlockState> getOreStates()
    {
        return oreStates.values();
    }

    @Nullable
    @Override
    public Indicator getIndicator()
    {
        return indicator;
    }

    @Override
    public boolean canGenerateIn(IBlockState state)
    {
        return stoneStates.contains(state);
    }

    @Override
    public boolean inRange(V vein, int xOffset, int zOffset)
    {
        return xOffset * xOffset + zOffset * zOffset < horizontalSize * horizontalSize * vein.getSize();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean matchesDimension(Dimension dimension)
    {
        ResourceLocation loc = IRegistry.DIMENSION_TYPE.getKey(dimension.getType());
        if (loc == null)
        {
            System.out.println("Loc is null");
            return true;
        }
        String name = loc.toString();
        if (dimensions == null)
        {
            return "minecraft:overworld".equals(name);
        }
        for (String dim : dimensions)
        {
            if (dim.equals(name))
            {
                return dimensionIsWhitelist;
            }
        }
        return !dimensionIsWhitelist;
    }

    @Override
    public boolean matchesBiome(Biome biome)
    {
        if (biomes == null) return true;
        for (String s : biomes)
        {
            //noinspection ConstantConditions
            String biomeName = biome.getRegistryName().getPath();
            if (biomeName.equals(s))
            {
                return biomesIsWhitelist;
            }
            for (BiomeDictionary.Type type : BiomeDictionary.getTypes(biome))
            {
                if (s.equalsIgnoreCase(type.getName()))
                {
                    return biomesIsWhitelist;
                }
            }
        }
        return !biomesIsWhitelist;
    }

    @Override
    public boolean isValid()
    {
        return oreStates != null && !oreStates.isEmpty() &&
                stoneStates != null && !stoneStates.isEmpty() &&
                (indicator == null || indicator.isValid()) &&
                maxY > minY && minY >= 0 &&
                count > 0 &&
                rarity > 0 &&
                verticalSize > 0 && horizontalSize > 0 && density > 0;

    }

    @Override
    public int getMinY()
    {
        return minY;
    }

    @Override
    public int getMaxY()
    {
        return maxY;
    }

    @Override
    public int getCount()
    {
        return count;
    }

    @Override
    public int getRarity()
    {
        return rarity;
    }

    @Override
    public int getChunkRadius()
    {
        return 1 + (horizontalSize >> 4);
    }

    @Override
    public String toString()
    {
        return String.format("[%s: Count: %d, Rarity: %d, Y: %d - %d, Size: %d / %d, Density: %2.2f, Ores: %s, Stones: %s]", VeinRegistry.getName(this), count, rarity, minY, maxY, horizontalSize, verticalSize, density, oreStates, stoneStates);
    }

    protected final BlockPos defaultStartPos(int chunkX, int chunkZ, Random rand)
    {
        return new BlockPos(
                chunkX * 16 + rand.nextInt(16),
                minY + rand.nextInt(maxY - minY),
                chunkZ * 16 + rand.nextInt(16)
        );
    }
}
