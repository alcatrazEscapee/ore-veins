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
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import com.alcatrazescapee.oreveins.command.ClearWorldCommand;
import com.alcatrazescapee.oreveins.command.FindVeinsCommand;
import com.alcatrazescapee.oreveins.command.VeinInfoCommand;
import com.alcatrazescapee.oreveins.util.VeinReloadListener;
import com.mojang.brigadier.CommandDispatcher;

public class ForgeEventHandler
{
    private final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event)
    {
        LOGGER.debug("On Server Starting");

        if (OreVeinsConfig.INSTANCE.debugCommands)
        {
            LOGGER.info("Registering Debug Commands");
            CommandDispatcher<CommandSource> dispatcher = event.getCommandDispatcher();

            ClearWorldCommand.register(dispatcher);
            FindVeinsCommand.register(dispatcher);
            VeinInfoCommand.register(dispatcher);
        }

        event.getServer().getResourceManager().addReloadListener(VeinReloadListener.INSTANCE);
    }
}
