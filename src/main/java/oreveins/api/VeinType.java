package oreveins.api;

import com.typesafe.config.Config;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class VeinType extends IForgeRegistryEntry.Impl<VeinType> {

    protected Ore ore;

    /**
     * Your vein class must subclass this. It also must have the following same two constructors as this class
     * In your implementation of VeinType, the constructor taking three arguments is used to generate a random vein at a position
     * You should also call super() to set the ore, otherwise a NPE will occur
     */
    public VeinType() {
    }

    public VeinType(Ore ore, BlockPos pos, Random rand) {
        this.ore = ore;
    }

    /**
     * Checks if the vein is in range from a horizontal position.
     *
     * @param pos The horizontal (x and z) positions
     * @return is the x and z position in range to spawn a block
     */
    public abstract boolean inRange(BlockPos pos);

    /**
     * Get the chance to generate an ore block at a specific position.
     *
     * @param pos The position to generate at
     * @return the chance to generate. Range is >=1 will generate 100% chance to <=0 will generate 0% chance
     */
    public abstract double getChanceToGenerate(BlockPos pos);


    /**
     * Creates an ore object to store in the list of all ores as added by json
     *
     * @param config The config object representing the json configuration of the ore
     * @return An ore object that has any additional paramaters added in the json
     * @link GenHandler has useful implementations of Config library for getting specific values
     */
    public abstract Ore createOre(Config config);

    /**
     * Gets the ore assigned to this vein
     *
     * @return The ore passed in
     */
    public final Ore getOre() {
        return ore;
    }

}
