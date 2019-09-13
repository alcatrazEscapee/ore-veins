/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.command.CommandSource;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import com.alcatrazescapee.oreveins.commands.ClearWorldCommand;
import com.alcatrazescapee.oreveins.commands.FindVeinsCommand;
import com.alcatrazescapee.oreveins.commands.VeinInfoCommand;
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

        if (Config.SERVER.debugCommands.get())
        {
            LOGGER.info("Registering Debug Commands");
            CommandDispatcher<CommandSource> dispatcher = event.getCommandDispatcher();

            ClearWorldCommand.register(dispatcher);
            FindVeinsCommand.register(dispatcher);
            VeinInfoCommand.register(dispatcher);
        }
    }
}
