/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */


package com.alcatrazescapee.oreveins;

import org.apache.commons.lang3.tuple.Pair;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public enum OreVeinsConfig
{
    INSTANCE;

    private static final ServerConfig SERVER;
    private static final ForgeConfigSpec SERVER_SPEC;

    static
    {
        final Pair<ServerConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_SPEC = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    public boolean noOres;
    public boolean debugCommands;
    public int extraChunkRange;

    public void setup()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_SPEC);
    }

    public void load()
    {
        noOres = SERVER.noOres.get();
        debugCommands = SERVER.debugCommands.get();
        extraChunkRange = SERVER.extraChunkRange.get();
    }

    public static class ServerConfig
    {
        private ForgeConfigSpec.BooleanValue noOres;
        private ForgeConfigSpec.BooleanValue debugCommands;
        private ForgeConfigSpec.IntValue extraChunkRange;

        ServerConfig(ForgeConfigSpec.Builder builder)
        {
            builder.push("general");

            noOres = builder
                    .comment("Stop all vanilla ore gen calls?")
                    .define("no_ores", true);

            debugCommands = builder
                    .comment("Enable debug commands such as /veininfo, /clearworld, /findveins")
                    .define("debug_commands", true);

            extraChunkRange = builder
                    .comment("Extra chunk search range when generating veins", "Use if your veins are getting cut off at chunk boundaries")
                    .defineInRange("extra_chunk_range", 0, 0, 20);

            builder.pop();
        }
    }
}
