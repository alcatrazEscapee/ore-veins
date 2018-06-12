package oreveins;


import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = OreVeins.MODID, version = OreVeins.VERSION)
public class OreVeins {

    static final String MODID = "notreepunching";
    static final String VERSION = "0.1";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new WorldGen());
        MinecraftForge.ORE_GEN_BUS.register(new WorldGen());
    }
}
