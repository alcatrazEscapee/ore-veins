/*
 * Part of the Realistic Ore Veins Mod by AlcatrazEscapee
 * Work under Copyright. See the project LICENSE.md for details.
 */

package com.alcatrazescapee.oreveins.world;

import java.util.*;
import java.util.stream.Collectors;

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
        disabledBlockStates = Config.COMMON.disabledBlockStates();
        disableAll = Config.COMMON.noOres.get();

        ForgeRegistries.BIOMES.forEach(biome -> {
            List<ConfiguredFeature<?>> features = DISABLED_FEATURES.computeIfAbsent(biome, key -> new ArrayList<>());

            List<ConfiguredFeature<?>> toReAdd = features.stream().filter(x -> !shouldDisable(x)).collect(Collectors.toList());
            List<ConfiguredFeature<?>> toRemove = biome.getFeatures(GenerationStage.Decoration.UNDERGROUND_ORES).stream().filter(VanillaFeatureManager::shouldDisable).collect(Collectors.toList());

            features.addAll(toRemove);
            features.removeAll(toReAdd);

            List<ConfiguredFeature<?>> currentFeatures = biome.getFeatures(GenerationStage.Decoration.UNDERGROUND_ORES);
            currentFeatures.addAll(toReAdd);
            currentFeatures.removeAll(toRemove);
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
