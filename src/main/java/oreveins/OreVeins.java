package oreveins;


import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import oreveins.world.WorldGenReplacer;
import oreveins.world.WorldGenVeins;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

@Mod(modid = OreVeins.MODID, version = OreVeins.VERSION)
public class OreVeins {

    static final String MODID = "oreveins";
    static final String VERSION = "0.1";

    public static Logger log;

    @Mod.Instance
    private static OreVeins instance = new OreVeins();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        log = event.getModLog();
        MinecraftForge.EVENT_BUS.register(this);

        GenHandler.preInit(event.getModConfigurationDirectory());

        GameRegistry.registerWorldGenerator(new WorldGenVeins(), 1);
        MinecraftForge.ORE_GEN_BUS.register(new WorldGenReplacer());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        GenHandler.postInit();
    }

    @SubscribeEvent
    public static void configChanged(@Nonnull ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(MODID)) {
            ConfigManager.sync(MODID, net.minecraftforge.common.config.Config.Type.INSTANCE);
        }
    }
}
