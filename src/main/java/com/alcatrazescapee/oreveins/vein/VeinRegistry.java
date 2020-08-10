/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.vein;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;
import net.minecraft.block.state.IBlockState;

import com.alcatrazescapee.oreveins.OreVeins;
import com.alcatrazescapee.oreveins.OreVeinsConfig;
import com.alcatrazescapee.oreveins.api.ICondition;
import com.alcatrazescapee.oreveins.api.IVeinType;
import com.alcatrazescapee.oreveins.cmd.CommandClearWorld;
import com.alcatrazescapee.oreveins.util.IWeightedList;
import com.alcatrazescapee.oreveins.util.json.*;
import com.alcatrazescapee.oreveins.world.WorldGenVeins;

import static com.alcatrazescapee.oreveins.OreVeins.MOD_ID;

public final class VeinRegistry
{
    private static final BiMap<String, IVeinType> VEINS = HashBiMap.create();
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(IVeinType.class, new VeinTypeDeserializer())
            .registerTypeAdapter(new TypeToken<List<IBlockState>>() {}.getType(), new BlockStateListDeserializer())
            .registerTypeAdapter(new TypeToken<IWeightedList<IBlockState>>() {}.getType(), new WeightedListDeserializer<>(IBlockState.class))
            .registerTypeAdapter(new TypeToken<IWeightedList<Indicator>>() {}.getType(), new WeightedListDeserializer<>(Indicator.class))
            .registerTypeAdapter(IBlockState.class, new BlockStateDeserializer())
            .registerTypeAdapter(ICondition.class, new ConditionDeserializer())
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocationDeserializer())
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
                OreVeins.getLog().error("Error creating world gen config folder!");
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
        Path[] recursivePathList;
        try
        {
            recursivePathList = Files.walk(worldGenFolder.toPath()).filter(Files::isRegularFile).toArray(Path[]::new);
        }
        catch (IOException e)
        {
            OreVeins.getLog().error("Unable to read files in the config directory! No veins will be generated!");
            return;
        }

        for (Path path : recursivePathList)
        {
            try
            {
                // Read each file, then json parse each file individually into a map, so each vein can be parsed by GSON independently
                String fileContents = Files.readAllLines(path).stream().reduce((x, y) -> x + y).orElse("");
                Set<Map.Entry<String, JsonElement>> allVeinsJson = new JsonParser().parse(fileContents).getAsJsonObject().entrySet();
                for (Map.Entry<String, JsonElement> entry : allVeinsJson)
                {
                    try
                    {
                        IVeinType<?> vein = GSON.fromJson(entry.getValue(), IVeinType.class);
                        if (vein.isValid())
                        {
                            if (VEINS.containsKey(entry.getKey()))
                            {
                                OreVeins.getLog().error("Duplicate Veins found for the name {}. One has been discarded.", entry.getKey());
                            }
                            else
                            {
                                VEINS.put(entry.getKey(), vein);
                            }
                        }
                        else
                        {
                            OreVeins.getLog().error("Vein {} is invalid. This is likely caused by one or more required parameters being left out.", entry.getKey());
                        }
                    }
                    catch (JsonParseException e)
                    {
                        OreVeins.getLog().error("Vein {} failed to parse. This is most likely caused by incorrectly specified JSON.", entry.getKey());
                        OreVeins.getLog().error("Error: ", e);
                    }
                }
            }
            catch (IOException e)
            {
                OreVeins.getLog().error("Unable to open the file at {}, skipping.", path);
                OreVeins.getLog().error("Error: ", e);
            }
        }

        // Post Reloading
        CommandClearWorld.resetVeinStates();
        WorldGenVeins.resetChunkRadius();

        OreVeins.getLog().info("Registered {} Veins Successfully.", VeinRegistry.getVeins().size());
    }

    private VeinRegistry() {}
}
