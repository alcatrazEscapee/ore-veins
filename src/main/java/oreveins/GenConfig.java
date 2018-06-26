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
}
