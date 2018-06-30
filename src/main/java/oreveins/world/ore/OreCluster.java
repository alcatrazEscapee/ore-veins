package oreveins.world.ore;

import com.typesafe.config.Config;
import oreveins.api.Helper;
import oreveins.api.Ore;

public class OreCluster extends Ore {

    public int density;

    public OreCluster(Config config) {
        this.density = Helper.getValue(config, "density", 50);
    }
}
