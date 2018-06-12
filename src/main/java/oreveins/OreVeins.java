package oreveins;


import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = OreVeins.MODID, version = OreVeins.VERSION)
public class OreVeins {

    static final String MODID = "oreveins";
    static final String VERSION = "0.1";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        WorldGen.init();
        MinecraftForge.EVENT_BUS.register(this);
        GameRegistry.registerWorldGenerator(new WorldGen(),1);
        MinecraftForge.ORE_GEN_BUS.register(new WorldGen());
    }
}
