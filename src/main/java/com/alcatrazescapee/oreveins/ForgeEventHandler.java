/*
 * Part of the Realistic Ore Veins Mod by AlcatrazEscapee
 * Work under Copyright. See the project LICENSE.md for details.
 */

package com.alcatrazescapee.oreveins;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.command.CommandSource;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import com.alcatrazescapee.oreveins.command.ClearWorldCommand;
import com.alcatrazescapee.oreveins.command.FindVeinsCommand;
import com.alcatrazescapee.oreveins.command.VeinInfoCommand;
import com.alcatrazescapee.oreveins.world.vein.VeinManager;
import com.mojang.brigadier.CommandDispatcher;

public enum ForgeEventHandler
{
    INSTANCE;

    private final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public void beforeServerStart(FMLServerAboutToStartEvent event)
    {
        // Register vein reload listener
        LOGGER.debug("Before Server Start");
        event.getServer().getResourceManager().addReloadListener(VeinManager.INSTANCE);
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event)
    {
        LOGGER.debug("On Server Starting");

        if (Config.COMMON.debugCommands.get())
        {
            LOGGER.info("Registering Debug Commands");
            CommandDispatcher<CommandSource> dispatcher = event.getCommandDispatcher();

            ClearWorldCommand.register(dispatcher);
            FindVeinsCommand.register(dispatcher);
            VeinInfoCommand.register(dispatcher);
        }
    }
}
