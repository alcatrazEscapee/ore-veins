/*
 Part of the Ore Veins Mod by alcatrazEscapee
 Work under Copyright. Licensed under the GPL-3.0.
 See the project LICENSE.md for more information.
 */

package oreveins;

import net.minecraftforge.common.config.Config;

@Config(modid = OreVeins.MODID)
public class GenConfig {

    @Config.Comment("Stop all vanilla ore gen calls (OreGenEvent.GenerateMineable)")
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

    @Config.Comment("If you notice that your veins (especially large ones) are being cut off at chunk boundaries, then try increacing this value. Warning: it will have an impact on world generation performance, so don't go overboard.")
    @Config.RangeInt(min = 0, max = 10)
    public static int EXTRA_CHUNK_SEARCH_RANGE = 0;
}
