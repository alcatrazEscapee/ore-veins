/*
 Part of the Ore Veins Mod by alcatrazEscapee
 Work under Copyright. Licensed under the GPL-3.0.
 See the project LICENSE.md for more information.
 */

package oreveins.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedListMultimap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import oreveins.VeinRegistry;
import oreveins.api.Ore;
import oreveins.api.VeinType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WorldGenVeins implements IWorldGenerator {

    // This is the max chunk radius that is searched when trying to gather new veins
    // The larger this is, the larger veins can be (as blocks from them will generate in chunks that are farther away)
    // Make sure that veins won't try and go beyond this, it can cause strange generation issues. (chunks missing, cut off, etc.)
    public static int CHUNK_RADIUS;
    public static int MAX_RADIUS; // Max size for a vein is 2x this value

    public static ImmutableList<Ore> ORE_SPAWN_DATA;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {

        List<VeinType> veins = getNearbyVeins(chunkX, chunkZ, world.getSeed());
        if (veins.isEmpty()) return;

        int xoff = chunkX * 16 + 8;
        int zoff = chunkZ * 16 + 8;
        for (VeinType vein : veins) {
            if (doesMatchDims(vein.getOre().dims, world.provider.getDimension(), vein.getOre().dimensionIsWhitelist))
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        // Do checks here that are specific to the the horizontal position, not the vertical one
                        Biome biomeAt = world.getBiome(new BlockPos(xoff + x, 0, zoff + z));
                        if (!vein.inRange(new BlockPos(xoff + x, 0, zoff + z)) || !doesMatchBiome(vein.getOre().biomes, biomeAt, vein.getOre().biomesIsWhitelist))
                            continue;
                        for (int y = vein.getOre().minY; y <= vein.getOre().maxY; y++) {

                            final BlockPos posAt = new BlockPos(xoff + x, y, z + zoff);
                            IBlockState stoneState = world.getBlockState(posAt);
                            IBlockState oreState = getWeightedOre(vein.getOre().oreStates);

                            if (random.nextDouble() < vein.getChanceToGenerate(posAt) && vein.getOre().stoneStates.contains(stoneState))
                                world.setBlockState(posAt, oreState);
                        }
                    }
                }
        }
    }

    // Used to generate chunk
    @Nonnull
    private List<VeinType> getNearbyVeins(int chunkX, int chunkZ, long worldSeed) {
        List<VeinType> veins = new ArrayList<>();

        for (int x = -CHUNK_RADIUS; x <= CHUNK_RADIUS; x++) {
            for (int z = -CHUNK_RADIUS; z <= CHUNK_RADIUS; z++) {
                List<VeinType> vein = getVeinsAtChunk(chunkX + x, chunkZ + z, worldSeed);
                if (!vein.isEmpty()) veins.addAll(vein);
            }
        }
        return veins;
    }

    // Gets veins at a single chunk. Deterministic for a specific chunk x/z and world seed
    @Nonnull
    private List<VeinType> getVeinsAtChunk(int chunkX, int chunkZ, Long worldSeed) {
        Random rand = new Random(worldSeed + chunkX * 341873128712L + chunkZ * 132897987541L);
        List<VeinType> veins = new ArrayList<>();

        for (Ore ore : ORE_SPAWN_DATA) {
            for (int i = 0; i < ore.count; i++) {
                if (rand.nextInt(ore.rarity) == 0) {
                    BlockPos startPos = new BlockPos(
                        chunkX * 16 + rand.nextInt(16),
                        ore.minY + rand.nextInt(ore.maxY - ore.minY),
                        chunkZ * 16 + rand.nextInt(16)
                    );
                    VeinType v = VeinRegistry.get(ore.type);
                    if (v != null) {
                        try {
                            veins.add(v.getClass().getDeclaredConstructor(Ore.class, BlockPos.class, Random.class).newInstance(ore, startPos, rand));
                        } catch (Exception e) {
                            // Something happens?
                        }
                    }
                }
            }
        }
        return veins;
    }

    private IBlockState getWeightedOre(LinkedListMultimap<IBlockState, Integer> ores) {
        double completeWeight = 0.0;
        for (int i : ores.values())
            completeWeight += (double) i;
        double r = Math.random() * completeWeight;
        double countWeight = 0.0;
        for (Map.Entry<IBlockState, Integer> entry : ores.entries()) {
            countWeight += (double) entry.getValue();
            if (countWeight >= r)
                return entry.getKey();
        }
        throw new RuntimeException("Problem choosing IBlockState from weighted list");
    }

    private boolean doesMatchBiome(@Nullable List<String> biomes, Biome biome, boolean isWhitelist) {
        if (biomes == null) return true;
        for (String s : biomes) {
            ResourceLocation loc = biome.getRegistryName();
            if (loc != null && (s.equals(loc.getResourcePath()) || s.toUpperCase().equals(biome.getTempCategory().name())))
                return isWhitelist;
        }
        return !isWhitelist;
    }

    private boolean doesMatchDims(@Nullable List<Integer> dims, int dim, boolean isWhitelist) {
        if (dims == null) return dim == 0;
        for (int i : dims) {
            if (dim == i)
                return isWhitelist;
        }
        return !isWhitelist;
    }

}
