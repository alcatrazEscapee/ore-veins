/*
 Part of the Ore Veins Mod by alcatrazEscapee
 Work under Copyright. Licensed under the GPL-3.0.
 See the project LICENSE.md for more information.
 */

package oreveins;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;
import net.minecraft.block.state.IBlockState;
import oreveins.api.Ore;
import oreveins.api.OreVeinsApi;
import oreveins.api.Vein;
import oreveins.world.WorldGenVeins;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static oreveins.OreVeins.MODID;
import static oreveins.OreVeins.log;
import static oreveins.api.OreVeinsApi.getBoolean;
import static oreveins.api.OreVeinsApi.getValue;

public class GenHandler {

    private static File worldGenFolder;

    public static void preInit(File modConfigDir) {

        log.info("Loading or creating ore generation config file");

        worldGenFolder = new File(modConfigDir, MODID);

        if (!worldGenFolder.exists() && !worldGenFolder.mkdir())
            throw new Error("Problem creating Ore Veins config directory.");

        File defaultFile = new File(worldGenFolder, "ore_veins.json");
        String defaultData = null;
        if (defaultFile.exists()) {
            try {
                defaultData = FileUtils.readFileToString(defaultFile, Charset.defaultCharset());
            } catch (IOException e) {
                throw new Error("Error reading default file.", e);
            }
        }
        if (Strings.isNullOrEmpty(defaultData)) {
            try {
                FileUtils.copyInputStreamToFile(WorldGenVeins.class.getResourceAsStream("/assets/ore_veins.json"), defaultFile);
            } catch (IOException e) {
                throw new Error("Error copying data into default world gen file", e);
            }
        }
    }

    public static void postInit() {
        File[] worldGenFiles = worldGenFolder.listFiles((file, name) -> name != null && name.toLowerCase(Locale.US).endsWith(".json"));
        if (worldGenFiles == null) throw new Error("There are no valid files in the world gen directory");

        // Read files:
        List<Config> configEntries = new ArrayList<>();
        String worldGenData;
        Config config;
        for (File worldGenFile : worldGenFiles) {
            worldGenData = null;
            if (worldGenFile.exists()) {
                try {
                    worldGenData = FileUtils.readFileToString(worldGenFile, Charset.defaultCharset());
                } catch (IOException e) {
                    throw new Error("Error reading world gen file.", e);
                }
            }

            if (Strings.isNullOrEmpty(worldGenData)) {
                log.warn("There is no data in a world gen file.");
                continue;
            }

            try {
                config = ConfigFactory.parseString(worldGenData);
                configEntries.add(config);
            } catch (Throwable e) {
                throw new Error("Cannot Parse world gen file.", e);
            }
        }

        ImmutableList.Builder<Ore> b = ImmutableList.builder();
        // Parse Ore gen entries

        if (configEntries.isEmpty()) throw new Error("There are no valid config entries!");

        // Parse all config entries
        int maxRadius = 1;
        for (Config data : configEntries) {
            for (Map.Entry<String, ConfigValue> entry : data.root().entrySet()) {
                try {
                    if (entry.getValue().valueType() == ConfigValueType.OBJECT) {
                        try {
                            Ore ore = parseOreEntry(data.getConfig(entry.getKey()));
                            b.add(ore);
                            if (ore.horizontalSize >> 4 > maxRadius) maxRadius = ore.horizontalSize >> 4;
                        } catch (Exception e) {
                            log.warn("Generation entry '" + entry.getKey() + "' failed to parse correctly, skipping. Check that the json is valid.", e);
                        }
                    }
                } catch (Throwable e) {
                    log.warn("Generation entry '" + entry.getKey() + "' failed to parse correctly, skipping. Check that the json is valid.", e);
                }
            }
        }
        WorldGenVeins.ORE_SPAWN_DATA = b.build();
        WorldGenVeins.CHUNK_RADIUS = maxRadius + 1 + GenConfig.EXTRA_CHUNK_SEARCH_RANGE;
        WorldGenVeins.MAX_RADIUS = 16 * (maxRadius + 1 + GenConfig.EXTRA_CHUNK_SEARCH_RANGE);
        log.info("Max chunk search radius is " + WorldGenVeins.CHUNK_RADIUS);
    }

    @Nonnull
    private static Ore parseOreEntry(Config config) throws IllegalArgumentException {

        final String veinType = getGenType(config); // Required

        Vein v = VeinRegistry.get(veinType);
        if (v == null) {
            throw new IllegalArgumentException("Vein Type is not allowed to be null");
        }
        Ore ore; // Gets the Ore from the Vein
        try {
            ore = v.createOre(config);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unable to create ore from Vein Type + " + veinType, e);
        }

        // Required values
        ore.stoneStates = getStones(config);
        ore.type = veinType;

        // Values with defaults
        ore.count = getValue(config, "count", 1);
        ore.rarity = getValue(config, "rarity", 10);
        ore.minY = getValue(config, "min_y", 16);
        ore.maxY = getValue(config, "max_y", 64);
        ore.verticalSize = getValue(config, "vertical_size", 15);
        ore.horizontalSize = getValue(config, "horizontal_size", 8);

        ore.biomes = getBiomes(config);
        ore.dims = getDims(config);

        ore.dimensionIsWhitelist = getBoolean(config, "dimensions_is_whitelist", true);
        ore.biomesIsWhitelist = getBoolean(config, "biomes_is_whitelist", true);

        return ore;
    }

    @Nonnull
    private static List<IBlockState> getStones(Config config) throws IllegalArgumentException {
        String key = "stone";
        List<IBlockState> states = new ArrayList<>();

        if (config.getValue(key).valueType() == ConfigValueType.LIST) {
            config.getConfigList(key).forEach(c -> states.add(OreVeinsApi.getBlockState(c)));
        } else if (config.getValue(key).valueType() == ConfigValueType.OBJECT) {
            states.add(OreVeinsApi.getBlockState(config.getConfig(key)));

        } else if (config.getValue(key).valueType() == ConfigValueType.STRING) {
            states.add(OreVeinsApi.getBlockState(config.getString(key)));

        } else {
            throw new IllegalArgumentException("Stone entry is not in the correct format");
        }
        return states;
    }

    private static String getGenType(Config config) throws IllegalArgumentException {
        String result;
        try {
            result = config.getString("type");
            if (VeinRegistry.get(result) == null) {
                throw new IllegalArgumentException("Vein Type " + result + " is not registered.");
            }
        } catch (Exception e) {
            result = "clusters";
        }
        return result;
    }

    @Nullable
    private static List<String> getBiomes(Config config) {
        try {
            List<String> biomes = config.getStringList("biomes");
            return biomes.isEmpty() ? null : biomes;
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    private static List<Integer> getDims(Config config) {
        try {
            List<Integer> dims = config.getIntList("dimensions");
            return dims.isEmpty() ? null : dims;
        } catch (Exception e) {
            return null;
        }
    }

}
