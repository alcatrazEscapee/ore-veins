package oreveins.world.ore;

import com.typesafe.config.Config;
import oreveins.api.GenHandler;
import oreveins.api.Ore;

public class OreCluster extends Ore {

    public int density;

    public OreCluster(Config config) {
        this.density = GenHandler.getValue(config, "density", 50);
    }
}
