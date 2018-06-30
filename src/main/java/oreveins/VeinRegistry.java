package oreveins;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import oreveins.api.Vein;

import javax.annotation.Nullable;

public class VeinRegistry {

    private static IForgeRegistry<Vein> registry;

    public static void init() {
        registry = new RegistryBuilder<Vein>()
                .setType(Vein.class)
                .setName(new ResourceLocation(OreVeins.MODID, "veins"))
                .create();
    }

    @Nullable
    public static Vein get(String key) {
        if (!registry.containsKey(new ResourceLocation(key))) {
            return null;
        }
        return registry.getValue(new ResourceLocation(key));
    }
}
