package oreveins;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldGen implements IWorldGenerator{

    // This is the max chunk radius that is searched when trying to gather new veins
    // The larger this is, the larger veins can be (as blocks from them will generate in chunks that are farther away)
    // Make sure that veins won't try and go beyond this, it can cause strange generation issues. (chunks missing, cut off, etc.)
    public static final int CHUNK_RADIUS = 2;
    public static final int MAX_RADIUS = 16 * CHUNK_RADIUS; // Max size for a vein is 2x this value
    public static final int MAX_RADIUS_SQUARED = MAX_RADIUS*MAX_RADIUS;

    private static double totalWeight;

    public static void init(){
        totalWeight = 0;
        for(EnumOreType ore : EnumOreType.values()){
            totalWeight += ore.getWeight();
        }
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if(world.provider.getDimension() == 0){
            //System.out.println("Random int: "+ random.nextInt()+ " at "+ chunkX+" "+chunkZ);
            generateChunk(world, chunkX, chunkZ, random);
        }
    }

    // Event based generation
    /*@SubscribeEvent
    public void onPopulateChunkPost(PopulateChunkEvent.Post event) {
        generateChunk(event.getWorld(), event.getChunkX(), event.getChunkZ());
    }*/

    private void generateChunk(World world, int chunkX, int chunkZ, Random rand){

        // Get veins in nearby chunks
        // This needs to be based on world seed as well
        List<IVeinType> veins = getNearbyVeins(chunkX, chunkZ, world.getSeed());
        //Random rand = new Random(world.getSeed() + chunkX * 341873128712L + chunkZ * 132897987541L);

        // Procedurally replace stone from nearby chunks
        if(veins.isEmpty()) return;

        int xoff = chunkX*16+8;
        int zoff = chunkZ*16+8;
        for(int x = 0; x < 16; x++){
            for(int z = 0; z < 16; z++){
                for(IVeinType vein : veins) {
                    if(vein == null || !vein.inRange(new BlockPos(xoff + x, 0, zoff + z))) continue;
                    for (int y = vein.getLowestY(); y < vein.getHighestY(); y++) {
                        // Replace stone at (xoff+x, y, zoff+z)
                        // Based on veins and vein.getChanceToGenerate
                        // Use rand
                        BlockPos posAt = new BlockPos(xoff + x, y, z + zoff);
                        if (world.getBlockState(posAt).getBlock() == Blocks.STONE) {

                            if (rand.nextDouble() < vein.getChanceToGenerate(posAt)) {
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
        Random rand = new Random(worldSeed + chunkX * 341873128712L + chunkZ * 132897987541L);

        if(rand.nextDouble() < totalWeight){
            EnumOreType oreType = getRandomOre(rand);
            EnumVeinType veinType = EnumVeinType.values()[rand.nextInt(EnumVeinType.values().length)];

            IVeinType vein = new VeinTypeCluster(oreType.getState(),
                    chunkX * 16 + rand.nextInt(16),
                    16 + rand.nextInt(64),
                    chunkZ * 16 + rand.nextInt(16),
                    veinType.getHeight(), veinType.getRadius(), veinType.getDensity()); // These can also be affected by randomness

            //System.out.println("VEIN AT: "+vein.getPos()+" TYPE: "+veinType.toString());
            veins.add(vein);
        }
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
    @Nonnull
    private EnumOreType getRandomOre(Random rand){
        double r = rand.nextDouble() * totalWeight;
        double countWeight = 0.0;
        for (EnumOreType ore : EnumOreType.values()) {
            countWeight += ore.getWeight();
            if (countWeight >= r)
                return ore;
        }
        throw new RuntimeException("Problem choosing random ore weights. Should never be shown");
    }






    // This is here for convenience of checking the results
    @SubscribeEvent
    public void onGenerateMineable(OreGenEvent.GenerateMinable event) {
        if (event.getWorld().provider.getDimension() == 0) {
            event.setResult(Event.Result.DENY);
        }
    }

    public enum EnumVeinType{
        SMALL_CLUSTER(0.4D, 10, 4),
        SMALL_BALL(0.7D, 10, 4),
        SMALL_PILLAR(0.4D, 4, 12),
        MEDIUM_CLUSTER(0.4D, 15, 8),
        MEDIUM_BALL(0.6D, 15, 8),
        MEDIUM_PILLAR(0.5D, 5, 16),
        LARGE_CLUSTER(0.2D, 16, 12),
        LARGE_PILLAR(0.3D, 7.5, 20);

        private final double density;
        private final double radius;
        private final double height;

        EnumVeinType(double density, double radius, double height){
            this.density = density;
            this.radius = radius;
            this.height = height;
        }

        public double getDensity() {
            return density;
        }

        public double getHeight() {
            return height;
        }

        public double getRadius() {
            return radius;
        }
    }

    public enum EnumOreType{
        IRON_ORE(Blocks.IRON_ORE.getDefaultState(), 10),
        GOLD_ORE(Blocks.GOLD_ORE.getDefaultState(), 40),
        DIAMOND_ORE(Blocks.DIAMOND_ORE.getDefaultState(), 60),
        REDSTONE_ORE(Blocks.REDSTONE_ORE.getDefaultState(), 40),
        LAPIS_ORE(Blocks.LAPIS_ORE.getDefaultState(), 60),
        COAL_ORE(Blocks.COAL_ORE.getDefaultState(), 20);

        private final IBlockState state;
        private final double weight;

        EnumOreType(IBlockState state, int perXChunks){
            this.state = state;
            this.weight = 1.0D / (double)perXChunks;
        }

        public IBlockState getState() {
            return state;
        }

        public double getWeight() {
            return weight;
        }
    }

}
