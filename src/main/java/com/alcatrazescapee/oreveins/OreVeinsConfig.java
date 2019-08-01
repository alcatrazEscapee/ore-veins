/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */


package com.alcatrazescapee.oreveins;

import net.minecraftforge.common.config.Config;

@SuppressWarnings("WeakerAccess")
@Config(modid = OreVeins.MOD_ID)
public class OreVeinsConfig
{
    @Config.Comment("Stop all vanilla ore gen calls (OreGenEvent.GenerateMineable)")
    public static boolean NO_ORES = false;

    @Config.Comment({"Specific ore types to stop. Only used if NO_ORES is false. Allowed values:",
            "ANDESITE, DIORITE, COAL, CUSTOM, DIAMOND, DIRT, EMERALD, GOLD, GRANITE, GRAVEL, IRON, LAPIS, QUARTZ, REDSTONE, SILVERFISH"})
    public static String[] STOPPED_ORES = {
            "COAL",
            "DIAMOND",
            "EMERALD",
            "GOLD",
            "IRON",
            "LAPIS",
            "REDSTONE"
    };

    @Config.Comment({"This is a list of dimensions in which to stop vanilla / common modded ore generation calls. By default it is a blacklist.", "Note, this ONLY applies to stopped vanilla ores, veins have their own dimension config settings"})
    public static int[] STOPPED_ORES_DIMENSIONS = {};

    @Config.Comment({"Should the list of dimensions to stop ores in be a whitelist?", "Note, this ONLY applies to stopped vanilla ores, veins have their own dimension config settings"})
    public static boolean STOPPED_ORES_DIMENSIONS_IS_WHITELIST = false;

    @Config.Comment("If you notice that your veins (especially large ones) are being cut off at chunk boundaries, then try increasing this value. Warning: it will have an impact on world generation performance, so don't go overboard.")
    @Config.RangeInt(min = 0, max = 10)
    public static int EXTRA_CHUNK_SEARCH_RANGE = 0;

    @Config.Comment("Enables debug commands /clearworld, /veininfo, /reloadveins and /findveins")
    @Config.RequiresMcRestart
    public static boolean DEBUG_COMMANDS = true;

    @Config.Comment("Always create default ore_veins.json file if none if found.")
    public static boolean ALWAYS_CREATE_DEFAULT_CONFIG = true;

    @Config.Comment("Try and avoid cutting off veins at the min / max of the range by not creating centers at the min / max of the y-range")
    public static boolean AVOID_VEIN_CUTOFFS = true;
}
