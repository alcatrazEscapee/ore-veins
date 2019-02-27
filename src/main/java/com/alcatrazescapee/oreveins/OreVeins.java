/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.command.CommandSource;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.CompositeFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import com.alcatrazescapee.oreveins.cmd.CommandClearWorld;
import com.alcatrazescapee.oreveins.cmd.CommandFindVeins;
import com.alcatrazescapee.oreveins.cmd.CommandVeinInfo;
import com.alcatrazescapee.oreveins.vein.VeinRegistry;
import com.alcatrazescapee.oreveins.world.AtChunk;
import com.alcatrazescapee.oreveins.world.FeatureVeins;
import com.mojang.brigadier.CommandDispatcher;

@Mod(OreVeins.MOD_ID)
public class OreVeins
{
    public static final String MOD_ID = "oreveins";

    private static final Logger LOGGER = LogManager.getLogger();

    public OreVeins()
    {
        LOGGER.info("Init " + MOD_ID);
        // Config
        OreVeinsConfig.INSTANCE.setup();

        // Mod Event Bus (for @SubscribeEvent annotations on the mod bus)
        FMLJavaModLoadingContext.get().getModEventBus().register(this);

        // Forge Event Bus (for the single method that is on the forge bus)
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);
    }

    @SubscribeEvent
    public void onLoadConfig(final ModConfig.Loading event)
    {
        if (event.getConfig().getType() == ModConfig.Type.SERVER)
        {
            OreVeinsConfig.INSTANCE.load();
            // remove all other ore veins
            if (OreVeinsConfig.INSTANCE.noOres)
            {
                ForgeRegistries.BIOMES.forEach(biome -> biome.getFeatures(GenerationStage.Decoration.UNDERGROUND_ORES).removeIf(x -> x.getFeature() == Feature.MINABLE));
            }
        }
    }

    @SubscribeEvent
    public void setup(final FMLCommonSetupEvent event)
    {
        // Setup Vein Config Folder
        VeinRegistry.preInit();

        // World Gen
        ForgeRegistries.BIOMES.forEach(biome -> {
            CompositeFeature<?, ?> feature = Biome.createCompositeFeature(new FeatureVeins(), new NoFeatureConfig(), new AtChunk(), IPlacementConfig.NO_PLACEMENT_CONFIG);
            biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, feature);
        });

        // After Veins have Reloaded
        CommandClearWorld.resetVeinStates();
        FeatureVeins.resetChunkRadius();
    }

    private void onServerStarting(final FMLServerStartingEvent event)
    {
        if (OreVeinsConfig.INSTANCE.debugCommands)
        {
            LOGGER.info("Registering Debug Commands");
            CommandDispatcher<CommandSource> dispatcher = event.getCommandDispatcher();

            CommandClearWorld.register(dispatcher);
            CommandFindVeins.register(dispatcher);
            CommandVeinInfo.register(dispatcher);
        }
    }
}
