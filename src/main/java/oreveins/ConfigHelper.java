/*
 Part of the Ore Veins Mod by alcatrazEscapee
 Work under Copyright. Licensed under the GPL-3.0.
 See the project LICENSE.md for more information.
 */

package oreveins;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.LinkedListMultimap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigValueType;
import mcp.MethodsReturnNonnullByDefault;

@MethodsReturnNonnullByDefault
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

    public static List<IBlockState> getBlockStateList(Config config, String key) throws IllegalArgumentException
    {
        List<IBlockState> states = new ArrayList<>();

        if (config.getValue(key).valueType() == ConfigValueType.LIST)
        {
            config.getConfigList(key).forEach(c -> states.add(ConfigHelper.getBlockState(c)));
        }
        else if (config.getValue(key).valueType() == ConfigValueType.OBJECT)
        {
            states.add(ConfigHelper.getBlockState(config.getConfig(key)));

        }
        else if (config.getValue(key).valueType() == ConfigValueType.STRING)
        {
            states.add(ConfigHelper.getBlockState(config.getString(key)));

        }
        else
        {
            throw new IllegalArgumentException("Entry '" + key + "' is not in the correct format");
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
    public static LinkedListMultimap<IBlockState, Integer> getWeightedBlockStateList(Config config, String key) throws IllegalArgumentException
    {
        LinkedListMultimap<IBlockState, Integer> states = LinkedListMultimap.create();

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
            throw new IllegalArgumentException("Ore entry is not in the correct format");
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
    @SuppressWarnings("deprecation")
    public static IBlockState getBlockState(Config config) throws IllegalArgumentException
    {
        try
        {
            String name = config.getString("block");
            int meta = getValue(config, "meta", -1);
            Block block = Block.getBlockFromName(name);
            if (block == null) throw new IllegalArgumentException("Block is null when getting block from Config");
            return (meta == -1) ? block.getDefaultState() : block.getStateFromMeta(meta);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Unable to parse IBlockState from Config");
        }
    }

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