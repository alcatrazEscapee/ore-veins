package oreveins;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import com.google.common.collect.ImmutableList;
import com.google.gson.*;
import org.apache.logging.log4j.core.util.Loader;

import static oreveins.OreVeins.log;

public class WorldGen implements IWorldGenerator{

    // This is the max chunk radius that is searched when trying to gather new veins
    // The larger this is, the larger veins can be (as blocks from them will generate in chunks that are farther away)
    // Make sure that veins won't try and go beyond this, it can cause strange generation issues. (chunks missing, cut off, etc.)
    private static final int CHUNK_RADIUS = 2;
    public static final int MAX_RADIUS = 16 * CHUNK_RADIUS; // Max size for a vein is 2x this value
    public static final int MAX_RADIUS_SQUARED = MAX_RADIUS*MAX_RADIUS;

    public static File configDir;

    private static ImmutableList<WorldGen.Ore> ORE_SPAWN_DATA;
    private static String genData;
    private static Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    public static void preInit()
    {
        log.info("Loading or creating ore generation config file");

        File tfcDir = new File(configDir, "/ore_veins/");

        if (!tfcDir.exists())
        {
            try
            {
                if (!tfcDir.mkdir())
                {
                    throw new Error("Problem creating ore gen config directory: (unknown error)");
                }
            }
            catch (Exception e)
            {
                log.fatal("Problem creating ore gen config directory:", e);
                return;
            }
        }
        File genFile = new File(tfcDir, "ore_veins.json");
        Path genPath = genFile.toPath();
        try
        {
            if (genFile.createNewFile())
            {
                copyDefaultOreGen(genFile);
                log.info("Created standard generation json.");
            }
            else if (!genFile.exists())
            {
                throw new Error("Problem creating TFC world gen json: (unspecified error).");
            }
        }
        catch (Exception e)
        {
            log.fatal("Problem creating TFC world gen json: ", e);
        }
        try
        {
            genData = new String(Files.readAllBytes(genPath));
        }
        catch (IOException e)
        {
            log.fatal("Unable to read world gen json.", e);
        }

        log.info("Complete.");
    }

    public static void postInit()
    {
        Set<Map.Entry<String, JsonElement>> entries;
        ImmutableList.Builder<WorldGen.Ore> builder = new ImmutableList.Builder<>();

        try
        {
            JsonElement rootJson = new JsonParser().parse(genData);
            JsonObject rootObject = rootJson.getAsJsonObject();

            entries = rootObject.entrySet();
        }
        catch (Exception e)
        {
            // Problems
            return;
        }

        for (Map.Entry<String, JsonElement> entry : entries)
        {
            OreEntry genEntry;
            final IBlockState state;
            final int rarity;
            final int minY;
            final int maxY;
            final int density;
            final int horizontalSize;
            final int verticalSize;

            try
            {
                genEntry = GSON.fromJson(entry.getValue(), OreEntry.class);
                rarity = genEntry.rarity;
                minY = genEntry.minY;
                maxY = genEntry.maxY;
                density = genEntry.density;
                horizontalSize = genEntry.horizontalSize;
                verticalSize = genEntry.verticalSize;

                String blockName = genEntry.ore;
                Block block = Block.getBlockFromName(blockName);
                if (block != null)
                {
                    state = block.getDefaultState();
                }
                else
                {
                    log.warn("Problem parsing IBlockState: block doesn't exist for ore generation entry with key: \"" + entry.getKey() + "\" Skipping.");
                    continue;
                }
            }
            catch (Exception e)
            {
                //Problems
                log.warn("Problem parsing data for ore generation entry with key: \"" + entry.getKey() + "\" Skipping.");
                continue;
            }

            builder.add(new Ore(state, rarity, minY, maxY, density, horizontalSize, verticalSize));
            log.debug("Added ore generation entry for " + entry.getKey());
        }
        ORE_SPAWN_DATA = builder.build();
        log.info("Added "+ORE_SPAWN_DATA.size()+" ore generation entries.");
    }

    private static void copyDefaultOreGen(File dest) throws IOException
    {
        InputStream is = Loader.getResource("assets/ore_veins.json", null).openStream();
        OutputStream os = new FileOutputStream(dest);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0)
        {
            os.write(buffer, 0, length);
        }
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, net.minecraft.world.World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        // Check dimension is overworld
        if (world.provider.getDimension() != 0) return;

        List<Vein> veins = getNearbyVeins(chunkX, chunkZ, world.getSeed());
        if (veins.isEmpty()) return;


        int xoff = chunkX*16+8;
        int zoff = chunkZ*16+8;
        for (Vein vein : veins)
        {
            for (int x = 0; x < 16; x++)
            {
                for (int z = 0; z < 16; z++)
                {
                    // Do checks here that are specific to the the horizontal position, not the vertical one
                    if (!vein.inRange(new BlockPos(xoff + x, 0, zoff + z))) continue;

                    for (int y = vein.getLowestY(); y <= vein.getHighestY(); y++)
                    {

                        final BlockPos posAt = new BlockPos(xoff + x, y, z + zoff);
                        if (random.nextDouble() < vein.getChanceToGenerate(posAt))
                            world.setBlockState(posAt, vein.ore.state);
                    }
                }
            }
        }
    }

    // Used to generate chunk
    private List<Vein> getNearbyVeins(int chunkX, int chunkZ, long worldSeed)
    {
        List<Vein> veins = new ArrayList<>();

        for (int x = -CHUNK_RADIUS; x <= CHUNK_RADIUS; x++)
        {
            for (int z = -CHUNK_RADIUS; z <= CHUNK_RADIUS; z++)
            {
                List<Vein> vein = getVeinsAtChunk(chunkX + x, chunkZ + z, worldSeed);
                if (!vein.isEmpty()) veins.addAll(vein);
            }
        }
        return veins;
    }

    // Gets veins at a single chunk. Deterministic for a specific chunk x/z and world seed
    @Nonnull
    private List<Vein> getVeinsAtChunk(int chunkX, int chunkZ, Long worldSeed)
    {
        Random rand = new Random(worldSeed + chunkX * 341873128712L + chunkZ * 132897987541L);
        List<Vein> veins = new ArrayList<>();

        for(Ore ore : ORE_SPAWN_DATA)
        {
            if (rand.nextInt(ore.rarity) == 0)
            {
                BlockPos startPos = new BlockPos(
                        chunkX * 16 + rand.nextInt(16),
                        ore.minY + rand.nextInt(ore.maxY - ore.minY),
                        chunkZ * 16 + rand.nextInt(16)
                );
                Vein vein = new Vein(ore, startPos, rand);
                veins.add(vein);
            }
        }
        return veins;
    }

    @SubscribeEvent
    public void onGenerateMineable(OreGenEvent.GenerateMinable event) {
        if (event.getWorld().provider.getDimension() == 0) {
            event.setResult(Event.Result.DENY);
        }
    }

    private final class OreEntry{

        private String ore;
        private int rarity;
        private int minY;
        private int maxY;
        private int density;
        private int horizontalSize;
        private int verticalSize;

    }
    public static final class Ore{

        public final IBlockState state;

        public final int rarity;
        public final int minY;
        public final int maxY;
        public final int density;
        public final int horizontalSize;
        public final int verticalSize;

        Ore(@Nonnull IBlockState state, int rarity, int minY, int maxY, int density, int horizontalSize, int verticalSize){
            this.state = state;
            this.rarity = rarity;
            this.minY = minY;
            this.maxY = maxY;
            this.density = density;
            this.horizontalSize = horizontalSize;
            this.verticalSize = verticalSize;
        }
    }


}
