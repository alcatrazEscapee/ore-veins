/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.vein;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import net.minecraft.block.state.IBlockState;

import com.alcatrazescapee.oreveins.OreVeins;
import com.alcatrazescapee.oreveins.api.IVeinType;
import com.alcatrazescapee.oreveins.cmd.CommandClearWorld;
import com.alcatrazescapee.oreveins.util.IWeightedList;
import com.alcatrazescapee.oreveins.util.json.BlockStateDeserializer;
import com.alcatrazescapee.oreveins.util.json.BlockStateListDeserializer;
import com.alcatrazescapee.oreveins.util.json.VeinTypeDeserializer;
import com.alcatrazescapee.oreveins.util.json.WeightedListDeserializer;
import com.alcatrazescapee.oreveins.world.WorldGenVeins;

import static com.alcatrazescapee.oreveins.OreVeins.MOD_ID;

public final class VeinRegistry
{
    private static final BiMap<String, IVeinType> VEINS = HashBiMap.create();
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(IVeinType.class, new VeinTypeDeserializer())
            .registerTypeAdapter(new TypeToken<List<IBlockState>>() {}.getType(), new BlockStateListDeserializer())
            .registerTypeAdapter(IWeightedList.class, new WeightedListDeserializer())
            .registerTypeAdapter(IBlockState.class, new BlockStateDeserializer())
            .create();
    private static File worldGenFolder;

    public static Collection<IVeinType> getVeins()
    {
        return VEINS.values();
    }

    public static Set<String> getNames()
    {
        return VEINS.keySet();
    }

    public static IVeinType getVein(String key)
    {
        return VEINS.get(key);
    }

    public static String getName(IVeinType key)
    {
        return VEINS.inverse().get(key);
    }

    public static void preInit(File modConfigDir)
    {
        OreVeins.getLog().info("Loading or creating ore generation config file");

        worldGenFolder = new File(modConfigDir, MOD_ID);

        if (!worldGenFolder.exists() && !worldGenFolder.mkdir())
            throw new Error("Problem creating Ore Veins config directory.");

        File defaultFile = new File(worldGenFolder, "ore_veins.json");
        String defaultData = null;
        if (defaultFile.exists())
        {
            try
            {
                defaultData = FileUtils.readFileToString(defaultFile, Charset.defaultCharset());
            }
            catch (IOException e)
            {
                throw new Error("Error reading default file.", e);
            }
        }
        if (Strings.isNullOrEmpty(defaultData))
        {
            try
            {
                FileUtils.copyInputStreamToFile(WorldGenVeins.class.getResourceAsStream("/assets/ore_veins.json"), defaultFile);
            }
            catch (IOException e)
            {
                throw new Error("Error copying data into default world gen file", e);
            }
        }
    }

    public static void reloadVeins()
    {
        File[] worldGenFiles = worldGenFolder.listFiles((file, name) -> name != null && name.toLowerCase(Locale.US).endsWith(".json"));
        if (worldGenFiles == null) throw new Error("There are no valid files in the world gen directory");
        String worldGenData;
        for (File worldGenFile : worldGenFiles)
        {
            worldGenData = null;
            if (worldGenFile.exists())
            {
                try
                {
                    worldGenData = FileUtils.readFileToString(worldGenFile, Charset.defaultCharset());
                }
                catch (IOException e)
                {
                    OreVeins.getLog().warn("Error reading world gen file.", e);
                    continue;
                }
            }

            if (Strings.isNullOrEmpty(worldGenData))
            {
                OreVeins.getLog().warn("There is no data in a world gen file.");
                continue;
            }

            try
            {
                Map<String, IVeinType> map = GSON.fromJson(worldGenData, new TypeToken<Map<String, IVeinType>>() {}.getType());
                VEINS.putAll(map);
                for (Map.Entry<String, IVeinType> entry : map.entrySet())
                {
                    if (entry.getValue().isValid())
                    {
                        VEINS.put(entry.getKey(), entry.getValue());
                    }
                    else
                    {
                        OreVeins.getLog().warn("Invalid vein {}, skipping.", entry.getKey());
                    }
                }
            }
            catch (Throwable e)
            {
                OreVeins.getLog().warn("Cannot parse a world gen file. Check the JSON is valid!", e);
            }
        }

        // Post Reloading
        CommandClearWorld.resetVeinStates();
        WorldGenVeins.resetSearchRadius();

        VEINS.values().forEach(OreVeins.getLog()::info);
    }
}
