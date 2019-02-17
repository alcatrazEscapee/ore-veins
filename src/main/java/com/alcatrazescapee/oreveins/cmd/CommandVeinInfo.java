/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.cmd;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextComponentString;

import com.alcatrazescapee.oreveins.api.IVeinType;
import com.alcatrazescapee.oreveins.vein.VeinRegistry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;


@ParametersAreNonnullByDefault
public final class CommandVeinInfo
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(
                Commands.literal("findveins").requires(source -> source.hasPermissionLevel(2))
                        .then(Commands.argument("type", StringArgumentType.word())
                                .executes(cmd -> veinInfo(cmd.getSource(), StringArgumentType.getString(cmd, "type")))
                        ));
    }

    private static int veinInfo(CommandSource source, String veinName)
    {
        source.sendFeedback(new TextComponentString("Registered Veins: "), true);

        // Search for veins that match a type
        final IVeinType type = VeinRegistry.getVein(veinName);
        if (type == null)
        {
            source.sendErrorMessage(new TextComponentString("Vein supplied does not match any valid vein names"));
        }
        else
        {
            source.sendFeedback(new TextComponentString("> Vein Type: " + type.toString()), true);
        }
        return 1;
    }
}
