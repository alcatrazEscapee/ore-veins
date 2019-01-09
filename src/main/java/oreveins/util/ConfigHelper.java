/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package oreveins.util;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigValueType;
import gnu.trove.map.hash.TObjectIntHashMap;

@ParametersAreNonnullByDefault
public class ConfigHelper
{

    /**
     * Gets an int value from an ore config with default value
     *
     * @param config       The ore config object
     * @param key          The key to check
     * @param defaultValue If not found, the default value
     * @return the value
     */
    public static int getValue(Config config, String key, int defaultValue)
    {
        int result;
        try
        {
            result = config.getInt(key);
        }
        catch (ConfigException e)
        {
            result = defaultValue;
        }
        return result;
    }

    /**
     * Gets a float value from an ore config with default value
     *
     * @param config       The ore config object
     * @param key          The key to check
     * @param defaultValue If not found, the default value
     * @return the value
     */
    public static float getValue(Config config, String key, float defaultValue)
    {
        float result;
        try
        {
            result = (float) config.getDouble(key);
        }
        catch (ConfigException e)
        {
            result = defaultValue;
        }
        return result;
    }

    /**
     * Gets a boolean value from a ore config with default value
     *
     * @param config the ore config object
     * @param key    the key to check
     * @return the value
     */
    public static boolean getBoolean(Config config, String key, boolean defaultValue)
    {
        boolean result;
        try
        {
            result = config.getBoolean(key);
        }
        catch (ConfigException e)
        {
            result = defaultValue;
        }
        return result;
    }

    /**
     * Gets a list of blockstates
     *
     * @param config The config object
     * @param key    The key
     * @return A list of blockstates
     * @throws IllegalArgumentException if the config object is not in the correct format (either string, string list, or object list)
     */
    @Nonnull
    public static List<IBlockState> getBlockStateList(Config config, String key) throws IllegalArgumentException
    {
        List<IBlockState> states = new ArrayList<>();

        if (config.getValue(key).valueType() == ConfigValueType.LIST)
        {
            config.getConfigList(key).forEach(c -> states.add(getBlockState(c)));
        }
        else if (config.getValue(key).valueType() == ConfigValueType.OBJECT)
        {
            states.add(getBlockState(config.getConfig(key)));

        }
        else if (config.getValue(key).valueType() == ConfigValueType.STRING)
        {
            states.add(getBlockState(config.getString(key)));
        }
        else
        {
            throw new IllegalArgumentException("Entry '" + key + "' is not in the correct format");
        }
        if (states.isEmpty())
        {
            throw new IllegalArgumentException("Entry '" + key + "' has no entries in its block state list");
        }
        return states;
    }

    /**
     * Gets a weighted block state list from a config object
     * Json format could be a single string, a block entry, or a weighted list of block entries
     *
     * @param config the ore entry config object
     * @param key    the key to check
     * @return a linked list of the blockstate(s)
     * @throws IllegalArgumentException if a problem was found trying to find the block states
     */
    @Nonnull
    public static TObjectIntHashMap<IBlockState> getWeightedBlockStateList(Config config, String key) throws IllegalArgumentException
    {
        TObjectIntHashMap<IBlockState> states = new TObjectIntHashMap<>();
        try
        {
            if (config.getValue(key).valueType() == ConfigValueType.LIST)
            {
                config.getConfigList(key).forEach(c -> states.put(getBlockState(c), getValue(c, "weight", 1)));
            }
            else if (config.getValue(key).valueType() == ConfigValueType.OBJECT)
            {
                states.put(getBlockState(config.getConfig(key)), getValue(config.getConfig(key), "weight", 1));

            }
            else if (config.getValue(key).valueType() == ConfigValueType.STRING)
            {
                states.put(getBlockState(config.getString(key)), 1);
            }
            else
            {
                throw new IllegalArgumentException("Weighted list entry is not of type LIST, OBJECT or STRING");
            }
        }
        catch (ConfigException e)
        {
            throw new IllegalArgumentException("Weighted list entry is not found!");
        }
        return states;
    }

    /**
     * Gets a single block state from a registry name (such as "minecraft:dirt")
     *
     * @param name the registry name
     * @return the block state
     * @throws IllegalArgumentException if the block state was not found
     */
    @Nonnull
    public static IBlockState getBlockState(String name) throws IllegalArgumentException
    {
        try
        {
            Block block = Block.getBlockFromName(name);
            if (block == null) throw new IllegalArgumentException("Block is null when getting block from String");
            return block.getDefaultState();
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Unable to parse IBlockState from String");
        }
    }

    /**
     * Gets a single block state from a config object containing a registry name and metadata
     *
     * @param config the config object
     * @return the block state
     * @throws IllegalArgumentException if the block state was not found
     */
    @Nonnull
    public static IBlockState getBlockState(Config config) throws IllegalArgumentException
    {
        try
        {
            String name = config.getString("block");
            int meta = getValue(config, "meta", -1);
            Block block = Block.getBlockFromName(name);
            if (block == null) throw new IllegalArgumentException("Block is null when getting block from Config");
            //noinspection deprecation
            return (meta == -1) ? block.getDefaultState() : block.getStateFromMeta(meta);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Unable to parse IBlockState from Config");
        }
    }

    /**
     * Gets a string list from a config object
     * @param config The config object
     * @param key the key to search
     * @return The string list, or null if not found / empty
     */
    @Nullable
    public static List<String> getStringList(Config config, String key)
    {
        try
        {
            List<String> biomes = config.getStringList(key);
            return biomes.isEmpty() ? null : biomes;
        }
        catch (ConfigException e)
        {
            return null;
        }
    }

    /**
     * Gets an int list from a config object
     * @param config The config object
     * @param key The key
     * @return The int list, or null if not found / empty
     */
    @Nullable
    public static List<Integer> getIntList(Config config, String key)
    {
        try
        {
            List<Integer> dims = config.getIntList(key);
            return dims.isEmpty() ? null : dims;
        }
        catch (ConfigException e)
        {
            return null;
        }
    }
}