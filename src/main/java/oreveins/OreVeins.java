/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package oreveins;


import org.apache.logging.log4j.Logger;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ICrashCallable;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import oreveins.cmd.CommandClearWorld;
import oreveins.cmd.CommandFindVeins;
import oreveins.cmd.CommandVeinInfo;
import oreveins.world.WorldGenReplacer;
import oreveins.world.WorldGenVeins;

@SuppressWarnings({"WeakerAccess", "unused"})
@Mod(modid = OreVeins.MOD_ID, version = OreVeins.VERSION, dependencies = OreVeins.DEPENDENCIES, acceptableRemoteVersions = "*", certificateFingerprint = "3c2d6be715971d1ed58a028cdb3fae72987fc934")
public class OreVeins
{
    public static final String MOD_ID = "oreveins";
    public static final String MOD_NAME = "Ore Veins";
    public static final String VERSION = "GRADLE:VERSION";

    private static final String FORGE_MIN = "14.23.2.2611";
    private static final String FORGE_MAX = "15.0.0.0";

    public static final String DEPENDENCIES = "required-after:forge@[" + FORGE_MIN + "," + FORGE_MAX + ");";

    private static Logger log;
    private boolean isSignedBuild;

    public static Logger getLog()
    {
        return log;
    }

    // This is necessary in order to catch the NewRegistry Event
    public OreVeins()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void configChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MOD_ID))
        {
            ConfigManager.sync(MOD_ID, Config.Type.INSTANCE);
            WorldGenVeins.resetSearchRadius();
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        log = event.getModLog();
        log.debug("If you can see this, debug logging is working :)");
        if (!isSignedBuild)
            log.warn("You are not running an official build. This version will NOT be supported by the author.");

        RegistryManager.preInit(event.getModConfigurationDirectory());

        GameRegistry.registerWorldGenerator(new WorldGenVeins(), 1);
        MinecraftForge.ORE_GEN_BUS.register(new WorldGenReplacer());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        if (!isSignedBuild)
            log.warn("You are not running an official build. This version will NOT be supported by the author.");
        RegistryManager.registerAllVeins();
    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent event)
    {
        if (OreVeinsConfig.DEBUG_COMMANDS)
        {
            event.registerServerCommand(new CommandClearWorld());
            event.registerServerCommand(new CommandVeinInfo());
            event.registerServerCommand(new CommandFindVeins());
        }
    }

    @Mod.EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent event)
    {
        isSignedBuild = false;
        FMLCommonHandler.instance().registerCrashCallable(new ICrashCallable()
        {
            @Override
            public String getLabel()
            {
                return MOD_NAME;
            }

            @Override
            public String call()
            {
                return "You are not running an official build. This version will NOT be supported by the author.";
            }
        });
    }
}
