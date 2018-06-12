package oreveins;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldGen{

    // This is the max chunk radius that is searched when trying to gather new veins
    // The larger this is, the larger veins can be (as blocks from them will generate in chunks that are farther away)
    // Make sure that veins won't try and go beyond this, it can cause strange generation issues. (chunks missing, cut off, etc.)
    public static final int CHUNK_RADIUS = 2;
    public static final int MAX_RADIUS = 16 * CHUNK_RADIUS; // Max size for a vein is 2x this value

    // Event based generation
    @SubscribeEvent
    public void onPopulateChunkPost(PopulateChunkEvent.Post event) {
        generateChunk(event.getWorld(), event.getChunkX(), event.getChunkZ());
    }

    private void generateChunk(World world, int chunkX, int chunkZ){

        // Get veins in nearby chunks
        // This needs to be based on world seed as well
        List<IVeinType> veins = getNearbyVeins(chunkX, chunkZ, world.getSeed());
        Random rand = new Random(world.getSeed() + chunkX * 341873128712L + chunkZ * 132897987541L);

        // Procedurally replace stone from nearby chunks
        if(veins.isEmpty()) return;

        int xoff = chunkX*16+8;
        int zoff = chunkZ*16+8;
        for(int x = 0; x < 16; x++){
            for(int z = 0; z < 16; z++){
                for(int y = 0; y < 128; y++){
                    // Replace stone at (xoff+x, y, zoff+z)
                    // Based on veins and vein.getChanceToGenerate
                    // Use rand
                    BlockPos posAt = new BlockPos(xoff + x, y, z + zoff);
                    if(world.getBlockState(posAt).getBlock() == Blocks.STONE){

                        IVeinType vein = getRandomVein(veins, posAt, rand);
                        if(vein != null){
                            if(rand.nextDouble() < vein.getChanceToGenerate(posAt)){
                                world.setBlockState(posAt, vein.getBlockState());
                            }
                        }
                    }
                }
            }
        }
    }

    // Used to generate chunk
    private List<IVeinType> getNearbyVeins(int chunkX, int chunkZ, long worldSeed){
        List<IVeinType> veins = new ArrayList<>();

        for(int x = -CHUNK_RADIUS; x <= CHUNK_RADIUS; x++){
            for(int z = -CHUNK_RADIUS; z <= CHUNK_RADIUS; z++){

                veins.addAll(getVeinsAtChunk(chunkX + x, chunkZ + z, worldSeed));
            }
        }

        return veins;
    }

    // Gets veins at a single chunk. Deterministic for a specific chunk x/z and world seed
    // Here is where vein properties would be determined (type, ore, size, properties, etc.)
    // All random values has to use rand.
    private List<IVeinType> getVeinsAtChunk(int chunkX, int chunkZ, Long worldSeed){
        List<IVeinType> veins = new ArrayList<>();

        Random rand = new Random(worldSeed * chunkX * 341873128712L + chunkZ * 132897987541L);
        if(rand.nextDouble() <= 0.05 && chunkX % 2 == 0 && chunkZ % 2 == 0){
            if(rand.nextBoolean()) {
                veins.add(new VeinTypeCluster(getState(rand),
                        chunkX * 16 + rand.nextInt(16),
                        16 + rand.nextInt(64),
                        chunkZ * 16 + rand.nextInt(16)));
            }else{
                veins.add(new VeinTypeVerticalColumn(getState(rand),
                        chunkX * 16 + rand.nextInt(16),
                        chunkZ * 16 + rand.nextInt(16)));
            }
        }
        // Deterministic vein getting based off chunkSeed

        return veins;
    }


    @Nullable
    private IVeinType getRandomVein(@Nonnull List<IVeinType> veins, BlockPos pos, Random rand){
        List<IVeinType> veinsInRange = new ArrayList<>(veins);
        for(IVeinType vein : veins){
            if(vein.inRange(pos)){
                veinsInRange.add(vein);
            }
        }
        if(veinsInRange.isEmpty()) return null;
        return veinsInRange.get(veinsInRange.size() == 1 ? 0 : rand.nextInt(veinsInRange.size() - 1));
    }
    // Helper methods.
    // In TFC these would be much more complicated
    @Nonnull
    private IBlockState getState(Random rand){
        return rand.nextBoolean() ? Blocks.GOLD_BLOCK.getDefaultState() : Blocks.IRON_BLOCK.getDefaultState();
    }






    // This is here for convenience of checking the results
    @SubscribeEvent
    public void onGenerateMineable(OreGenEvent.GenerateMinable event) {
        if (event.getWorld().provider.getDimension() == 0) {
            event.setResult(Event.Result.DENY);
        }
    }
}
