package oreveins;


import org.apache.logging.log4j.Logger;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = OreVeins.MODID, version = OreVeins.VERSION)
public class OreVeins {

    static final String MODID = "oreveins";
    static final String VERSION = "0.1";

    static Logger log;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);

        WorldGen.configDir = event.getModConfigurationDirectory();
        WorldGen.preInit();

        GameRegistry.registerWorldGenerator(new WorldGen(),1);
        MinecraftForge.ORE_GEN_BUS.register(new WorldGen());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event){
        WorldGen.postInit();
    }
}
