/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

import com.alcatrazescapee.oreveins.cmd.ClearWorldCommand;
import com.alcatrazescapee.oreveins.vein.VeinRegistry;
import com.alcatrazescapee.oreveins.world.AtChunk;
import com.alcatrazescapee.oreveins.world.VeinsFeature;

import static com.alcatrazescapee.oreveins.OreVeins.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod(MOD_ID)
public class OreVeins
{
    public static final String MOD_ID = "oreveins";

    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onLoadConfig(final ModConfig.Loading event)
    {
        LOGGER.debug("Loading Config");
        if (event.getConfig().getType() == ModConfig.Type.SERVER)
        {
            OreVeinsConfig.INSTANCE.load();
            // remove all other ore veins
            if (OreVeinsConfig.INSTANCE.noOres)
            {
                ForgeRegistries.BIOMES.forEach(biome -> {
                    biome.getFeatures(GenerationStage.Decoration.UNDERGROUND_ORES).removeIf(feature -> {
                        if (feature.config instanceof DecoratedFeatureConfig)
                        {
                            return ((DecoratedFeatureConfig) feature.config).feature.feature == Feature.ORE;
                        }
                        return false;
                    });
                });
            }
        }
    }

    @SubscribeEvent
    public static void setup(final FMLCommonSetupEvent event)
    {
        LOGGER.debug("Setup");
        // Setup Vein Config Folder
        VeinRegistry.preInit();

        // World Gen
        ForgeRegistries.BIOMES.forEach(biome -> {
            ConfiguredFeature<?> feature = Biome.createDecoratedFeature(new VeinsFeature(), new NoFeatureConfig(), new AtChunk(), IPlacementConfig.NO_PLACEMENT_CONFIG);
            biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, feature);
        });

        // After Veins have Reloaded
        ClearWorldCommand.resetVeinStates();
        VeinsFeature.resetChunkRadius();
    }

    public OreVeins()
    {
        LOGGER.info("Constructor");

        // Setup config
        OreVeinsConfig.INSTANCE.setup();
    }
}
