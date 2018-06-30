/*
 Part of the Ore Veins Mod by alcatrazEscapee
 Work under Copyright. Licensed under the GPL-3.0.
 See the project LICENSE.md for more information.
 */

package oreveins;


import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import oreveins.api.GenHandler;
import oreveins.api.VeinType;
import oreveins.world.WorldGenReplacer;
import oreveins.world.WorldGenVeins;
import oreveins.world.vein.VeinTypeCluster;
import org.apache.logging.log4j.Logger;

@Mod(modid = OreVeins.MODID, version = OreVeins.VERSION, dependencies = OreVeins.DEPENDENCIES)
public class OreVeins {

    private static final String FORGE_VERSION = "GRADLE:FORGE_VERSION";
    private static final String FORGE_VERSION_MAX = "15.0.0.0";

    public static final String MODID = "oreveins";
    static final String VERSION = "GRADLE:VERSION";
    static final String DEPENDENCIES = "required-after:forge@["+FORGE_VERSION+","+FORGE_VERSION_MAX+");";

    public static Logger log;

    // This is nessecary in order to catch the NewRegistry Event
    public OreVeins() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public static void configChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(MODID)) {
            ConfigManager.sync(MODID, net.minecraftforge.common.config.Config.Type.INSTANCE);
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        GenHandler.postInit();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        log = event.getModLog();
        log.info("Pre-init started");
        //MinecraftForge.EVENT_BUS.register(this);

        GenHandler.preInit(event.getModConfigurationDirectory());

        GameRegistry.registerWorldGenerator(new WorldGenVeins(), 1);
        MinecraftForge.ORE_GEN_BUS.register(new WorldGenReplacer());
    }

    @SubscribeEvent
    public void addRegistry(RegistryEvent.NewRegistry event) {
        VeinRegistry.init();
    }

    @SubscribeEvent
    public void addVeins(RegistryEvent.Register<VeinType> event) {
        log.info("Registering Ore Vein type cluster");
        event.getRegistry().register(new VeinTypeCluster().setRegistryName(MODID, "cluster"));
        event.getRegistry().getKeys().forEach(x -> log.info("There is a registry with key " + x));
    }
}
