/*
 Part of the Ore Veins Mod by alcatrazEscapee
 Work under Copyright. Licensed under the GPL-3.0.
 See the project LICENSE.md for more information.
 */

package oreveins.world.ore;

import com.google.common.collect.LinkedListMultimap;
import com.typesafe.config.Config;
import net.minecraft.block.state.IBlockState;
import oreveins.api.Ore;
import oreveins.api.OreVeinsApi;

public class OreCluster extends Ore {

    public LinkedListMultimap<IBlockState, Integer> oreStates;
    public int density;
    public int clusters;

    public OreCluster(Config config) throws IllegalArgumentException {
        super(config);
        this.density = OreVeinsApi.getValue(config, "density", 50);
        this.oreStates = OreVeinsApi.getWeightedBlockStateList(config, "ore");
        this.clusters = OreVeinsApi.getValue(config, "clusters", 3);
    }
}
