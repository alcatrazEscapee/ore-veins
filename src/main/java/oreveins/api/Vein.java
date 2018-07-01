/*
 Part of the Ore Veins Mod by alcatrazEscapee
 Work under Copyright. Licensed under the GPL-3.0.
 See the project LICENSE.md for more information.
 */

package oreveins.api;

import com.typesafe.config.Config;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

/**
 * Your vein class must subclass this. It also must have the following same two constructors as this class
 * In your implementation of VeinType, the constructor taking three arguments is used to generate a random vein at a position
 * You should also call super() to set the ore, otherwise a NPE will occur
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class Vein extends IForgeRegistryEntry.Impl<Vein> {

    protected Ore ore;

    public Vein() {
    }

    public Vein(Ore ore, BlockPos pos, Random rand) {
        this.ore = ore;
    }

    /**
     * Checks if the vein is in range from a horizontal position.
     *
     * @param xPos the x position to check
     * @param zPos the z position to check
     * @return is the x and z position in range to spawn a block
     */
    public abstract boolean inRange(int xPos, int zPos);

    /**
     * Get the chance to generate an ore block at a specific position.
     *
     * @param pos The position to generate at
     * @return the chance to generate. Range is >=1 will generate with 100% chance to <=0 will generate with 0% chance
     */
    public abstract double getChanceToGenerate(BlockPos pos);

    /**
     * Gets the IBlockState to generate at a specific position. For random values use Math.random()
     *
     * @param pos the position to generate at
     * @return an IBlockState representing the state to set
     */
    public abstract IBlockState getStateToGenerate(BlockPos pos);


    /**
     * Creates an ore object to store in the list of all ores as added by json
     *
     * @param config The config object representing the json configuration of the ore
     * @return An ore object that has any additional parameters added in the json
     *
     * @link Helper has useful implementations of Config library for getting specific values with defaults
     *       See VeinCluster for an example
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
