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
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import com.alcatrazescapee.oreveins.cmd.ClearWorldCommand;
import com.alcatrazescapee.oreveins.cmd.FindVeinsCommand;
import com.alcatrazescapee.oreveins.cmd.VeinInfoCommand;
import com.mojang.brigadier.CommandDispatcher;

import static com.alcatrazescapee.oreveins.OreVeins.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler
{
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onServerStarting(FMLServerStartingEvent event)
    {
        if (OreVeinsConfig.INSTANCE.debugCommands)
        {
            LOGGER.info("Registering Debug Commands");
            CommandDispatcher<CommandSource> dispatcher = event.getCommandDispatcher();

            ClearWorldCommand.register(dispatcher);
            FindVeinsCommand.register(dispatcher);
            VeinInfoCommand.register(dispatcher);
        }
    }
}
