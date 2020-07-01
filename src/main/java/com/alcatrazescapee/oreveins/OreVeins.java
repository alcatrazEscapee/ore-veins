/*
 * Part of the Realistic Ore Veins Mod by AlcatrazEscapee
 * Work under Copyright. See the project LICENSE.md for details.
 */

package com.alcatrazescapee.oreveins;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import com.alcatrazescapee.oreveins.world.ModFeatures;
import com.alcatrazescapee.oreveins.world.VanillaFeatureManager;

import static com.alcatrazescapee.oreveins.OreVeins.MOD_ID;

@Mod(MOD_ID)
public class OreVeins
{
    public static final String MOD_ID = "oreveins";

    private static final Logger LOGGER = LogManager.getLogger();

    public OreVeins()
    {
        LOGGER.debug("Constructing");

        // Setup config
        Config.register();

        // Register event handlers
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.register(this);
        ModFeatures.FEATURES.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(ForgeEventHandler.INSTANCE);
    }

    @SubscribeEvent
    @SuppressWarnings("deprecation")
    public void setup(final FMLCommonSetupEvent event)
    {
        LOGGER.debug("Setup");

        // World Gen - needs to be ran on main thread to avoid concurrency errors with multiple mods trying to do the same ore generation modifications.
        // Forge fix your stuff and either make not deprecate it or add an alternative.
        DeferredWorkQueue.runLater(() -> {
            ForgeRegistries.BIOMES.forEach(biome -> {
                ConfiguredFeature<?, ?> feature = ModFeatures.VEINS.get().withConfiguration(new NoFeatureConfig()).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG));
                biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, feature);
            });

            VanillaFeatureManager.onConfigReloading();
        });
    }

    @SubscribeEvent
    public void onLoadConfig(final ModConfig.Reloading event)
    {
        LOGGER.debug("Reloading config - reevaluating vanilla ore vein settings");
        if (event.getConfig().getType() == ModConfig.Type.SERVER)
        {
            VanillaFeatureManager.onConfigReloading();
        }
    }
}
