/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package oreveins.cmd;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import oreveins.RegistryManager;
import oreveins.vein.VeinType;

@ParametersAreNonnullByDefault
public class CommandVeinInfo extends CommandBase
{
    @Override
    @Nonnull
    public String getName()
    {
        return "veininfo";
    }

    @Override
    @Nonnull
    public String getUsage(ICommandSender sender)
    {
        return "/veininfo [all|<vein name>] -> lists info about registered veins. Use 'all' to see all registered veins";
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length == 1)
            return getListOfStringsMatchingLastWord(args, CommandClearWorld.veinNames);
        return Collections.emptyList();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length != 1) throw new WrongUsageException("1 argument required.");
        if (sender.getCommandSenderEntity() == null) throw new WrongUsageException("Can only be used by a player");

        sender.sendMessage(new TextComponentString("Registered Veins: "));
        if (args[0].equals("all"))
        {
            // Search for all veins
            RegistryManager.getVeins().keySet().forEach(x -> sender.sendMessage(new TextComponentString("> Vein Type: " + x)));
        }
        else
        {
            // Search for veins that match a type
            VeinType type = RegistryManager.getVeins().get(args[0]);
            if (type == null)
                throw new WrongUsageException("Vein supplied does not match 'all' or any valid vein names. Use /veininfo to see valid vein names");
            sender.sendMessage(new TextComponentString("> Vein Type: " + type.toString()));
        }
    }
}
