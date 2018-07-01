/*
 Part of the Ore Veins Mod by alcatrazEscapee
 Work under Copyright. Licensed under the GPL-3.0.
 See the project LICENSE.md for more information.
 */

package oreveins.api;

import net.minecraft.block.state.IBlockState;

import java.util.List;

/**
 * This class represents an type of ore vein. One of these is created for each entry in the json file
 * <p>
 * oreStates: List of BlockStates for the ores, which are linked to a weight value.
 * stoneStates: List of BlockStates for the stones in which the ore will spawn
 * type: Registry name of the Vein type
 * count: number of tries per chunk to spawn a vein
 * rarity: 1 / N chunks will have a vein of this type, on average
 * minY: minimum Y value to spawn ores
 * maxY: max Y value to spawn ores
 * horizontalSize: approximate maximum horizontal radius for the vein
 * verticalSize: approximate maximum vertical radius for the vein
 * biomes: list of biome names for the ore to spawn in
 * dims: list of dimension ids for the ore to spawn in
 * dimensionIsWhitelist: when false, dimension list becomes a blacklist
 * biomesIsWhitelist: when false, biome list becomes a blacklist
 */
public abstract class Ore {

    public List<IBlockState> stoneStates;
    public String type;

    public int count;
    public int rarity;
    public int minY;
    public int maxY;

    public int horizontalSize;
    public int verticalSize;

    public List<String> biomes;
    public List<Integer> dims;

    public boolean dimensionIsWhitelist;
    public boolean biomesIsWhitelist;

    public Ore() {
    }

}
