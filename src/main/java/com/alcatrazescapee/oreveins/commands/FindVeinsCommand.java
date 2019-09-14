/*
 * Part of the Realistic Ore Veins Mod by AlcatrazEscapee
 * Work under Copyright. See the project LICENSE.md for details.
 */

package com.alcatrazescapee.oreveins.commands;

import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.JsonParseException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;

import com.alcatrazescapee.oreveins.world.VeinsFeature;
import com.alcatrazescapee.oreveins.world.vein.Vein;
import com.alcatrazescapee.oreveins.world.vein.VeinManager;
import com.alcatrazescapee.oreveins.world.vein.VeinType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

@ParametersAreNonnullByDefault
public final class FindVeinsCommand
{
    private static final String TP_MESSAGE = "{\"text\":\"" + TextFormatting.BLUE + "[Click to Teleport]" + TextFormatting.RESET + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/tp %d %d %d\"}}";

    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(
                Commands.literal("findveins").requires(source -> source.hasPermissionLevel(2))
                        .then(Commands.argument("type", new VeinTypeArgument())
                                .then(Commands.argument("radius", IntegerArgumentType.integer(0, 250))
                                        .executes(cmd -> findVeins(cmd.getSource(), VeinTypeArgument.getVein(cmd, "type"), IntegerArgumentType.getInteger(cmd, "radius")))
                                )));
    }

    private static int findVeins(CommandSource source, ResourceLocation veinName, int radius) throws CommandSyntaxException
    {
        source.sendFeedback(new StringTextComponent("Veins Found: "), true);

        final BlockPos pos = new BlockPos(source.getPos());
        final int chunkX = pos.getX() >> 4, chunkZ = pos.getZ() >> 4;
        final List<Vein<?>> veins = VeinsFeature.getNearbyVeins(chunkX, chunkZ, source.getWorld().getSeed(), radius);
        final VeinType type = VeinManager.INSTANCE.getVein(veinName);
        if (type == null)
        {
            source.sendErrorMessage(new StringTextComponent("Vein supplied does not match any valid vein names"));
        }

        // Search for veins matching type
        veins.removeIf(x -> x.getType() != type);
        for (Vein<?> vein : veins)
        {
            ITextComponent resultText = new StringTextComponent("> Vein: " + vein.toString());
            if (source.getEntity() instanceof PlayerEntity)
            {
                BlockPos veinPos = vein.getPos();
                try
                {
                    ITextComponent tpText = ITextComponent.Serializer.fromJsonLenient(String.format(TP_MESSAGE, veinPos.getX(), veinPos.getY(), veinPos.getZ()));
                    if (tpText != null)
                    {
                        source.getEntity().sendMessage(TextComponentUtils.updateForEntity(source, resultText.appendSibling(tpText), source.getEntity(), 0));
                    }
                }
                catch (JsonParseException e) { /* Ignore, it shouldn't happen */ }
            }
        }
        return 1;
    }
}
