/*
 * Part of the Realistic Ore Veins Mod by AlcatrazEscapee
 * Work under Copyright. See the project LICENSE.md for details.
 */

package com.alcatrazescapee.oreveins;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import com.alcatrazescapee.oreveins.util.condition.DefaultVeinsCondition;
import com.alcatrazescapee.oreveins.world.AtChunk;
import com.alcatrazescapee.oreveins.world.VanillaFeatureManager;
import com.alcatrazescapee.oreveins.world.VeinsFeature;

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
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SERVER_SPEC);

        // Condition for default vein loading
        CraftingHelper.register(DefaultVeinsCondition.Serializer.INSTANCE);

        // Register event handlers
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        MinecraftForge.EVENT_BUS.register(ForgeEventHandler.INSTANCE);
    }

    @SubscribeEvent
    public void setup(final FMLCommonSetupEvent event)
    {
        LOGGER.debug("Setup");

        // World Gen
        ForgeRegistries.BIOMES.forEach(biome -> {
            ConfiguredFeature<?> feature = Biome.createDecoratedFeature(new VeinsFeature(), new NoFeatureConfig(), new AtChunk(), IPlacementConfig.NO_PLACEMENT_CONFIG);
            biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, feature);
        });
    }

    @SubscribeEvent
    public void onLoadConfig(final ModConfig.ConfigReloading event)
    {
        LOGGER.debug("Reloading config - reevaluating vanilla ore vein settings");
        if (event.getConfig().getType() == ModConfig.Type.SERVER)
        {
            VanillaFeatureManager.onConfigReloading();
        }
    }
}
