/*
 *  Part of the Ore Veins Mod by alcatrazEscapee
 *  Work under Copyright. Licensed under the GPL-3.0.
 *  See the project LICENSE.md for more information.
 */

package oreveins;


import org.apache.logging.log4j.Logger;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import oreveins.vein.VeinType;
import oreveins.world.WorldGenReplacer;
import oreveins.world.WorldGenVeins;

@Mod(modid = OreVeins.MOD_ID, version = OreVeins.VERSION, dependencies = OreVeins.DEPENDENCIES)
public class OreVeins
{

    private static final String FORGE_VERSION = "GRADLE:FORGE_VERSION";
    private static final String FORGE_VERSION_MAX = "15.0.0.0";

    static final String MOD_ID = "oreveins";
    static final String VERSION = "GRADLE:VERSION";
    static final String DEPENDENCIES = "required-after:forge@[" + FORGE_VERSION + "," + FORGE_VERSION_MAX + ");";

    private static Logger log;

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
    public static void configChanged(ConfigChangedEvent.OnConfigChangedEvent event)
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

        VeinRegistry.preInit(event.getModConfigurationDirectory());

        GameRegistry.registerWorldGenerator(new WorldGenVeins(), 1);
        MinecraftForge.ORE_GEN_BUS.register(new WorldGenReplacer());
    }

    @SubscribeEvent
    public void addRegistry(RegistryEvent.NewRegistry event)
    {
        VeinRegistry.createRegistry();
    }

    @SubscribeEvent
    public void addVeins(RegistryEvent.Register<VeinType> event)
    {
        VeinRegistry.registerAll(event.getRegistry());
    }
}
