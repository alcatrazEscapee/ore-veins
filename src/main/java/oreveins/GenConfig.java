package oreveins;

import net.minecraftforge.common.config.Config;

@Config(modid = OreVeins.MODID)
public class GenConfig {

    @Config.Comment("Stop all vanilla ore gen calls")
    public static boolean NO_ORES = false;

    @Config.Comment({"Specific ore types to stop. Only used if NO_ORES is false. Allowed values:",
            "ANDESITE, DIORITE, COAL, CUSTOM, DIAMOND, DIRT, EMERALD, GOLD, GRANITE, GRAVEL, IRON, LAPIS, QUARTZ, REDSTONE, SILVERFISH, "})
    public static String[] STOPPED_ORES = {
            "COAL",
            "DIAMOND",
            "EMERALD",
            "GOLD",
            "IRON",
            "LAPIS",
            "REDSTONE"
    };
}
