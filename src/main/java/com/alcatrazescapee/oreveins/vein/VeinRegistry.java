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
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import net.minecraft.block.state.IBlockState;

import com.alcatrazescapee.oreveins.OreVeins;
import com.alcatrazescapee.oreveins.OreVeinsConfig;
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
        if (OreVeinsConfig.ALWAYS_CREATE_DEFAULT_CONFIG)
        {
            if (!worldGenFolder.exists() && !worldGenFolder.mkdir())
            {
                OreVeins.getLog().warn("Error creating world gen config folder ");
            }
            else
            {
                // Config file exists, so verify that the default file is there as well
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
                        OreVeins.getLog().warn("Error reading default file.", e);
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
                        OreVeins.getLog().warn("Error copying data into default world gen file", e);
                    }
                }
            }
        }
    }

    public static void reloadVeins()
    {
        File[] worldGenFiles = worldGenFolder.listFiles((file, name) -> name != null && name.toLowerCase(Locale.US).endsWith(".json"));
        if (worldGenFiles == null)
        {
            OreVeins.getLog().error("There are no valid files in the world gen directory! This mod will not do anything!");
        }
        else
        {
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
                    Set<Map.Entry<String, JsonElement>> allVeinsJson = new JsonParser().parse(worldGenData).getAsJsonObject().entrySet();
                    for (Map.Entry<String, JsonElement> entry : allVeinsJson)
                    {
                        try
                        {
                            IVeinType<?> vein = GSON.fromJson(entry.getValue(), IVeinType.class);
                            if (vein.isValid())
                            {
                                if (VEINS.containsKey(entry.getKey()))
                                {
                                    OreVeins.getLog().warn("Duplicate Veins found for the name {}. One has been discarded.", entry.getKey());
                                }
                                else
                                {
                                    VEINS.put(entry.getKey(), vein);
                                }
                            }
                            else
                            {
                                OreVeins.getLog().warn("Vein {} is invalid. This is likely caused by one or more required parameters being left out.", entry.getKey());
                            }
                        }
                        catch (Throwable e)
                        {
                            OreVeins.getLog().warn("Vein {} failed to parse. This is most likely caused by incorrectly specified JSON.", entry.getKey());
                            OreVeins.getLog().warn("Error: ", e);
                        }
                    }
                }
                catch (Throwable e)
                {
                    OreVeins.getLog().warn("File {} failed to parse. This is most likely caused by invalid JSON. Error: {}", worldGenFile, e);
                    OreVeins.getLog().warn("Error: ", e);
                }
            }
        }

        // Post Reloading
        CommandClearWorld.resetVeinStates();
        WorldGenVeins.resetChunkRadius();

        OreVeins.getLog().info("Registered {} Veins Successfully.", VeinRegistry.getVeins().size());
    }

    private VeinRegistry() {}
}
