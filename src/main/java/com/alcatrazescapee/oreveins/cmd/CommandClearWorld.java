/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.cmd;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import com.alcatrazescapee.oreveins.RegistryManager;
import com.alcatrazescapee.oreveins.vein.VeinType;

@ParametersAreNonnullByDefault
public class CommandClearWorld extends CommandBase
{
    private static Set<IBlockState> veinStates;
    static Set<String> veinNames;

    public static void resetVeinStates()
    {
        veinStates = new HashSet<>();
        RegistryManager.getVeins()
                .values()
                .stream()
                .map(VeinType::getOreStates)
                .forEach(x -> veinStates.addAll(x));

        veinNames = new HashSet<>();
        veinNames.addAll(RegistryManager.getVeins().keySet());
        veinNames.add("all");
    }

    @Override
    @Nonnull
    public String getName()
    {
        return "clearworld";
    }

    @Override
    @Nonnull
    public String getUsage(ICommandSender sender)
    {
        return "/clearworld <radius> -> Removes all blocks that are NOT part of an ore vein";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length != 1) throw new WrongUsageException("1 argument required.");
        if (sender.getCommandSenderEntity() == null) throw new WrongUsageException("Can only be used by a player");

        sender.sendMessage(new TextComponentString("Clearing world... " + TextFormatting.RED + "Lag incoming"));

        final int radius = parseInt(args[0], 1, 250);
        final World world = sender.getEntityWorld();
        final BlockPos center = new BlockPos(sender.getCommandSenderEntity());
        final IBlockState AIR = Blocks.AIR.getDefaultState();

        for (int x = -radius; x <= radius; x++)
        {
            for (int z = -radius; z <= radius; z++)
            {
                for (int y = 255 - center.getY(); y >= -center.getY(); y--)
                {
                    final BlockPos pos = center.add(x, y, z);
                    if (!veinStates.contains(world.getBlockState(pos)))
                        world.setBlockState(pos, AIR, 2 | 16);
                }
            }
        }
        sender.sendMessage(new TextComponentString("Done."));
    }
}
