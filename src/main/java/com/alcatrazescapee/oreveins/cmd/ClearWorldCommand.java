/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.cmd;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import com.alcatrazescapee.oreveins.api.IVeinType;
import com.alcatrazescapee.oreveins.vein.VeinRegistry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

@ParametersAreNonnullByDefault
public final class ClearWorldCommand
{
    private static final Set<BlockState> VEIN_STATES = new HashSet<>();

    public static void resetVeinStates()
    {
        VeinRegistry.getVeins().stream().map(IVeinType::getOreStates).forEach(VEIN_STATES::addAll);
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(
                Commands.literal("clearworld").requires(source -> source.hasPermissionLevel(2))
                        .then(Commands.argument("radius", IntegerArgumentType.integer(1, 250))
                                .executes(cmd -> clearWorld(cmd.getSource(), IntegerArgumentType.getInteger(cmd, "radius")))));
    }

    private static int clearWorld(CommandSource source, int radius)
    {
        final World world = source.getWorld();
        final BlockPos center = new BlockPos(source.getPos());
        final BlockState air = Blocks.AIR.getDefaultState();

        for (int x = -radius; x <= radius; x++)
        {
            for (int z = -radius; z <= radius; z++)
            {
                for (int y = 255 - center.getY(); y >= -center.getY(); y--)
                {
                    final BlockPos pos = center.add(x, y, z);
                    if (!VEIN_STATES.contains(world.getBlockState(pos)) && !world.isAirBlock(pos))
                    {
                        world.setBlockState(pos, air, 2 | 16);
                    }
                }
            }
        }
        source.sendFeedback(new StringTextComponent("Done."), true);
        return 1;
    }
}
