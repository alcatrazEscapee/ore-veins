package oreveins;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import oreveins.api.VeinType;

import javax.annotation.Nullable;

public class VeinRegistry {

    private static IForgeRegistry<VeinType> registry;

    public static void init() {
        registry = new RegistryBuilder<VeinType>()
                .setType(VeinType.class)
                .setName(new ResourceLocation(OreVeins.MODID, "veins"))
                //.add(VeinRegistry::add)
                .create();
    }

    //public static void add(IForgeRegistryInternal<VeinType> owner, RegistryManager stage, int id, VeinType obj, @Nullable VeinType oldObj){ }

    @Nullable
    public static VeinType get(String key) {
        if (!registry.containsKey(new ResourceLocation(key))) {
            return null;
        }
        return registry.getValue(new ResourceLocation(key));
    }
}
