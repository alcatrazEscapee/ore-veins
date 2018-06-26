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
                        if(ore.horizontalSize >> 4 > maxRadius) maxRadius = ore.horizontalSize >> 4;
                    } catch (Exception e){
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
    }

    @Nonnull
    private static Ore parseOreEntry(Config config) throws IllegalArgumentException {
        log.info("Got a config: " + config);

        final int rarity = getValue(config, "rarity", 10);
        final int density = getValue(config, "density", 50);
        final int minY = getValue(config, "min_y", 16);
        final int maxY = getValue(config, "max_y", 64);
        final int verticalSize = getValue(config, "vertical_size", 15);
        final int horizontalSize = getValue(config, "horizontal_size", 8);
        final List<String> biomes = getBiomes(config);

        // If these entries are not present, the json will fail
        LinkedListMultimap<IBlockState, Integer> oreStates = getOres(config);
        List<IBlockState> stoneStates = getStones(config);

        return new Ore(oreStates, stoneStates, rarity, minY, maxY, density, horizontalSize, verticalSize, biomes);
    }
    @Nonnull
    private static List<IBlockState> getStones(Config config) throws IllegalArgumentException{
        log.info("Parsing a block list of some sort");
        String key = "stone";
        List<IBlockState> states = new ArrayList<>();

        if(config.getValue(key).valueType() == ConfigValueType.LIST){
            config.getConfigList(key).forEach(c -> {
                    states.add(getState(c));
            });
        }else if(config.getValue(key).valueType() == ConfigValueType.OBJECT){
            states.add(getState(config.getConfig(key)));

        }else if(config.getValue(key).valueType() == ConfigValueType.STRING){
            states.add(getState(config.getString(key)));

        }else{
            throw new IllegalArgumentException("Stone entry is not in the correct format");
        }
        return states;
    }
    @Nonnull
    private static LinkedListMultimap<IBlockState, Integer> getOres(Config config) throws IllegalArgumentException{
        log.info("Parsing a block list of some sort");
        String key = "ore";
        LinkedListMultimap<IBlockState, Integer> states = LinkedListMultimap.create();

        if(config.getValue(key).valueType() == ConfigValueType.LIST){
            config.getConfigList(key).forEach(c -> {
                states.put(getState(c), getValue(c, "weight", 1));
            });
        }else if(config.getValue(key).valueType() == ConfigValueType.OBJECT){
            states.put(getState(config.getConfig(key)), getValue(config.getConfig(key),"weight",1));

        }else if(config.getValue(key).valueType() == ConfigValueType.STRING){
            states.put(getState(config.getString(key)),1);

        }else{
            throw new IllegalArgumentException("Ore entry is not in the correct format");
        }
        return states;
    }
    @Nonnull
    private static IBlockState getState(String name) throws IllegalArgumentException{
        try{
            Block block = Block.getBlockFromName(name);
            if(block == null) throw new IllegalArgumentException("Block is null when getting block from String");
            return block.getDefaultState();
        }catch(Exception e){
            log.warn("Problem parsing block entry; Skipping");
            throw new IllegalArgumentException("Unable to parse IBlockState from String");
        }
    }

    @Nonnull @SuppressWarnings("deprecation")
    private static IBlockState getState(Config config) throws IllegalArgumentException{
        try {
            String name = config.getString("block");
            int meta = getValue(config, "meta", -1);
            Block block = Block.getBlockFromName(name);
            if(block == null) throw new IllegalArgumentException("Block is null when getting block from Config");
            return (meta == -1) ? block.getDefaultState() : block.getStateFromMeta(meta);
        }
        catch(Exception e) {
            throw new IllegalArgumentException("Unable to parse IBlockState from Config");
        }
    }
    private static int getValue(Config config, String key, int defaultValue){
        log.info("Reading a value: "+key);
        int result;
        try{
            result = config.getInt(key);
        }
        catch(Exception e){
            result = defaultValue;
        }
        return result;
    }
    @Nullable
    private static List<String> getBiomes(Config config){
        try{
            List<String> biomes = config.getStringList("biomes");
            return biomes.isEmpty() ? null : biomes;
        }
        catch(Exception e){
            return null;
        }
    }

    public static final class Ore {

        public final LinkedListMultimap<IBlockState, Integer> oreStates;
        public final List<IBlockState> stoneStates;

        public final int rarity;
        public final int minY;
        public final int maxY;
        public final int density;
        public final int horizontalSize;
        public final int verticalSize;

        public final List<String> biomes;

        public Ore(LinkedListMultimap<IBlockState, Integer> oreStates, List<IBlockState> stoneStates,
                   int rarity, int minY, int maxY, int density, int horizontalSize, int verticalSize, @Nullable List<String> biomes) {
            this.oreStates = oreStates;
            this.stoneStates = stoneStates;

            this.rarity = rarity;
            this.minY = minY;
            this.maxY = maxY;
            this.density = density;
            this.horizontalSize = horizontalSize;
            this.verticalSize = verticalSize;
            this.biomes = biomes;
        }
    }
}
