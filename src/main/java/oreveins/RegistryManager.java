/*
 *  Part of the Ore Veins Mod by alcatrazEscapee
 *  Work under Copyright. Licensed under the GPL-3.0.
 *  See the project LICENSE.md for more information.
 */


package oreveins;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;
import javax.annotation.Nonnull;

import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.commons.io.FileUtils;

import com.typesafe.config.*;
import oreveins.cmd.CommandClearWorld;
import oreveins.vein.*;
import oreveins.world.WorldGenVeins;

import static oreveins.OreVeins.MOD_ID;

public class RegistryManager
{
    private static BiMap<String, VeinType> registry = HashBiMap.create();
    private static File worldGenFolder;

    public static BiMap<String, VeinType> getVeins()
    {
        return registry;
    }

    static void preInit(File modConfigDir)
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

    static void registerAllVeins()
    {
        List<Config> entries = getAllOreEntries();

        // Parse all config entries
        int maxRadius = 1;
        for (Config data : entries)
        {
            for (Map.Entry<String, ConfigValue> entry : data.root().entrySet())
            {
                try
                {
                    if (entry.getValue().valueType() == ConfigValueType.OBJECT)
                    {
                        Config cfg = data.getConfig(entry.getKey());
                        Type type = getVeinSuperType(cfg);
                        String name = entry.getKey();
                        VeinType vein = type.supplier.apply(name, cfg); // This can throw an IllegalArgumentException, if the config was invalid for some reason
                        if (vein == null)
                            throw new IllegalArgumentException("Vein is null after initialization: this is probably a coding error.");
                        if (registry.containsKey(name))
                            throw new IllegalArgumentException("A vein by the key '" + name + "' already exists.");
                        registry.put(name, vein);
                        if (vein.horizontalSize >> 4 > maxRadius) maxRadius = vein.horizontalSize >> 4;
                        OreVeins.getLog().debug("Vein '" + entry.getKey() + "' parsed successfully and is now registered.");
                    }
                }
                catch (Exception e)
                {
                    OreVeins.getLog().warn("Generation entry '" + entry.getKey() + "' failed to parse correctly, skipping. Check that the json is valid.", e);
                }
            }
        }
        WorldGenVeins.resetSearchRadius(1 + maxRadius);
        CommandClearWorld.resetVeinStates();
    }

    private static List<Config> getAllOreEntries()
    {
        File[] worldGenFiles = worldGenFolder.listFiles((file, name) -> name != null && name.toLowerCase(Locale.US).endsWith(".json"));
        if (worldGenFiles == null) throw new Error("There are no valid files in the world gen directory");
        List<Config> configEntries = new ArrayList<>();
        String worldGenData;
        Config config;
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
                    throw new Error("Error reading world gen file.", e);
                }
            }

            if (Strings.isNullOrEmpty(worldGenData))
            {
                OreVeins.getLog().warn("There is no data in a world gen file.");
                continue;
            }

            try
            {
                config = ConfigFactory.parseString(worldGenData);
                configEntries.add(config);
            }
            catch (Throwable e)
            {
                throw new Error("Cannot Parse world gen file.", e);
            }
        }

        if (configEntries.isEmpty()) throw new Error("There are no valid config entries!");
        return configEntries;
    }

    private static Type getVeinSuperType(Config config)
    {
        try
        {
            String s = config.getString("type");
            return Type.valueOf(s.toUpperCase());
        }
        catch (ConfigException e)
        {
            OreVeins.getLog().warn("Vein does not have a type entry!!! This is bad. Falling back to sphere type.");
            return Type.SPHERE;
        }
        catch (IllegalArgumentException e)
        {
            OreVeins.getLog().warn("Vein type is not a valid type!!! This is bad. Falling back to sphere type.");
            return Type.SPHERE;
        }
    }

    @SuppressWarnings("unused")
    private enum Type
    {
        SPHERE(VeinTypeSphere::new),
        CLUSTER(VeinTypeCluster::new),
        CONE(VeinTypeCone::new),
        PIPE(VeinTypePipe::new);

        private BiFunction<String, Config, VeinType> supplier;

        Type(@Nonnull BiFunction<String, Config, VeinType> supplier)
        {
            this.supplier = supplier;
        }
    }
}
