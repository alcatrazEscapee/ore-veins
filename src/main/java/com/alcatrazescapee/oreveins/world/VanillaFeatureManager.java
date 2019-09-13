/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.world;

import java.util.*;

import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;
import net.minecraftforge.registries.ForgeRegistries;

import com.alcatrazescapee.oreveins.Config;

/**
 * Manages the removal and replacement of vanilla world gen features
 * Called on config reload
 *
 * @author AlcatrazEscapee
 */
public class VanillaFeatureManager
{
    private static final Map<Biome, List<ConfiguredFeature<?>>> DISABLED_FEATURES = new HashMap<>();

    private static Set<BlockState> disabledBlockStates;
    private static boolean disableAll;

    public static void onConfigReloading()
    {
        disabledBlockStates = Config.SERVER.disabledBlockStates();
        disableAll = Config.SERVER.noOres.get();

        ForgeRegistries.BIOMES.forEach(biome -> {
            // Re-add previously disabled features
            List<ConfiguredFeature<?>> features = DISABLED_FEATURES.computeIfAbsent(biome, key -> new ArrayList<>());
            for (ConfiguredFeature<?> feature : features)
            {
                if (!shouldDisable(feature))
                {
                    biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, feature);
                }
            }

            // Disable previously enabled features
            biome.getFeatures(GenerationStage.Decoration.UNDERGROUND_ORES).removeIf(VanillaFeatureManager::shouldDisable);
        });
    }

    private static boolean shouldDisable(ConfiguredFeature<?> feature)
    {
        if (feature.config instanceof DecoratedFeatureConfig)
        {
            Feature<?> oreFeature = ((DecoratedFeatureConfig) feature.config).feature.feature;
            if (oreFeature == Feature.ORE || oreFeature == Feature.EMERALD_ORE)
            {
                IFeatureConfig featureConfig = ((DecoratedFeatureConfig) feature.config).feature.config;
                if (featureConfig instanceof OreFeatureConfig)
                {
                    OreFeatureConfig oreConfig = (OreFeatureConfig) featureConfig;
                    return disableAll || disabledBlockStates.contains(oreConfig.state);
                }
            }
        }
        return false;
    }
}
