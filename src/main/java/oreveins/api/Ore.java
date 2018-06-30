package oreveins.api;

import com.google.common.collect.LinkedListMultimap;
import net.minecraft.block.state.IBlockState;

import java.util.List;

public abstract class Ore {

    public LinkedListMultimap<IBlockState, Integer> oreStates;
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
