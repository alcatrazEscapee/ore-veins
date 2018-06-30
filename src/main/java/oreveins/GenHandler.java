/*
 Part of the Ore Veins Mod by alcatrazEscapee
 Work under Copyright. Licensed under the GPL-3.0.
 See the project LICENSE.md for more information.
 */

package oreveins;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedListMultimap;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import oreveins.api.Ore;
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
import java.util.Map;

import static oreveins.OreVeins.MODID;
import static oreveins.OreVeins.log;
import static oreveins.api.Helper.getBoolean;
import static oreveins.api.Helper.getValue;

public class GenHandler {

    private static File worldGenFile;

    public static void preInit(File modConfigDir) {

        log.info("Loading or creating ore generation config file");

        File configFile = new File(modConfigDir, MODID);

        if (!configFile.exists() && !configFile.mkdir())
            throw new Error("Problem creating Ore Veins config directory.");

        worldGenFile = new File(configFile, "ore_veins.json");
    }

    public static void postInit() {
        // Read file into worldGenData
        String worldGenData = null;
        if (worldGenFile.exists()) {
            try {
                worldGenData = FileUtils.readFileToString(worldGenFile, Charset.defaultCharset());
            } catch (IOException e) {
                throw new Error("Error reading world gen file.", e);
            }
        }
        if (Strings.isNullOrEmpty(worldGenData)) {
            try {
                FileUtils.copyInputStreamToFile(WorldGenVeins.class.getResourceAsStream("/assets/ore_veins.json"), worldGenFile);
                worldGenData = FileUtils.readFileToString(worldGenFile, Charset.defaultCharset());
            } catch (IOException e) {
                throw new Error("Error copying data into world gen file", e);
            }
        }

        if (Strings.isNullOrEmpty(worldGenData)) {
            log.warn("There is no data in the world gen file: This mod will not do anything. Seek medical assistance");
            return;
        }

        ImmutableList.Builder<Ore> b = ImmutableList.builder();
        // Parse Ore gen entries
        Config data;
        try {
            data = ConfigFactory.parseString(worldGenData);
        } catch (Throwable e) {
            throw new Error("Cannot Parse world gen file.", e);
        }
        int maxRadius = 1;
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
                throw new Error("Cannot Parse world gen file.", e);
            }
        }
        WorldGenVeins.ORE_SPAWN_DATA = b.build();
        WorldGenVeins.CHUNK_RADIUS = maxRadius + 1;
        WorldGenVeins.MAX_RADIUS = 16 * (maxRadius + 1);
        log.info("Max radius is 1 + " + maxRadius);
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
        ore.oreStates = getOres(config);
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

        ore.dimensionIsWhitelist = getBoolean(config, "dimensions_is_whitelist");
        ore.biomesIsWhitelist = getBoolean(config, "biomes_is_whitelist");

        return ore;
    }

    @Nonnull
    private static List<IBlockState> getStones(Config config) throws IllegalArgumentException {
        String key = "stone";
        List<IBlockState> states = new ArrayList<>();

        if (config.getValue(key).valueType() == ConfigValueType.LIST) {
            config.getConfigList(key).forEach(c -> states.add(getState(c)));
        } else if (config.getValue(key).valueType() == ConfigValueType.OBJECT) {
            states.add(getState(config.getConfig(key)));

        } else if (config.getValue(key).valueType() == ConfigValueType.STRING) {
            states.add(getState(config.getString(key)));

        } else {
            throw new IllegalArgumentException("Stone entry is not in the correct format");
        }
        return states;
    }

    @Nonnull
    private static LinkedListMultimap<IBlockState, Integer> getOres(Config config) throws IllegalArgumentException {
        String key = "ore";
        LinkedListMultimap<IBlockState, Integer> states = LinkedListMultimap.create();

        if (config.getValue(key).valueType() == ConfigValueType.LIST) {
            config.getConfigList(key).forEach(c -> states.put(getState(c), getValue(c, "weight", 1)));
        } else if (config.getValue(key).valueType() == ConfigValueType.OBJECT) {
            states.put(getState(config.getConfig(key)), getValue(config.getConfig(key), "weight", 1));

        } else if (config.getValue(key).valueType() == ConfigValueType.STRING) {
            states.put(getState(config.getString(key)), 1);

        } else {
            throw new IllegalArgumentException("Ore entry is not in the correct format");
        }
        return states;
    }

    @Nonnull
    private static IBlockState getState(String name) throws IllegalArgumentException {
        try {
            Block block = Block.getBlockFromName(name);
            if (block == null) throw new IllegalArgumentException("Block is null when getting block from String");
            return block.getDefaultState();
        } catch (Exception e) {
            log.warn("Problem parsing block entry; Skipping");
            throw new IllegalArgumentException("Unable to parse IBlockState from String");
        }
    }

    @Nonnull
    @SuppressWarnings("deprecation")
    private static IBlockState getState(Config config) throws IllegalArgumentException {
        try {
            String name = config.getString("block");
            int meta = getValue(config, "meta", -1);
            Block block = Block.getBlockFromName(name);
            if (block == null) throw new IllegalArgumentException("Block is null when getting block from Config");
            return (meta == -1) ? block.getDefaultState() : block.getStateFromMeta(meta);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to parse IBlockState from Config");
        }
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
