/*
 * Part of the Realistic Ore Veins Mod by AlcatrazEscapee
 * Work under Copyright. See the project LICENSE.md for details.
 */

package com.alcatrazescapee.oreveins.command;

import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.JsonParseException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import com.alcatrazescapee.oreveins.world.VeinsFeature;
import com.alcatrazescapee.oreveins.world.vein.Vein;
import com.alcatrazescapee.oreveins.world.vein.VeinManager;
import com.alcatrazescapee.oreveins.world.vein.VeinType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import static com.alcatrazescapee.oreveins.OreVeins.MOD_ID;

@ParametersAreNonnullByDefault
public final class FindVeinsCommand
{
    private static final String TP_MESSAGE = "{\"text\":\"" + TextFormatting.BLUE + "[Click to Teleport]" + TextFormatting.RESET + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/tp %d %d %d\"}}";

    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(
            Commands.literal("findveins").requires(source -> source.hasPermissionLevel(2))
                .then(Commands.argument("type", new VeinTypeArgument())
                    .suggests((context, builder) -> ISuggestionProvider.suggestIterable(VeinManager.INSTANCE.getKeys(), builder))
                    .then(Commands.argument("radius", IntegerArgumentType.integer(0, 250))
                        .executes(cmd -> findVeins(cmd.getSource(), VeinTypeArgument.getVein(cmd, "type"), IntegerArgumentType.getInteger(cmd, "radius")))
                    )
                )
        );
    }

    private static int findVeins(CommandSource source, ResourceLocation veinName, int radius) throws CommandSyntaxException
    {
        final BlockPos pos = new BlockPos(source.getPos());
        final int chunkX = pos.getX() >> 4, chunkZ = pos.getZ() >> 4;
        final List<Vein<?>> veins = VeinsFeature.getNearbyVeins(chunkX, chunkZ, source.getWorld().getSeed(), radius);
        final VeinType<?> type = VeinManager.INSTANCE.getVein(veinName);
        if (type == null)
        {
            source.sendErrorMessage(new TranslationTextComponent(MOD_ID + ".command.unknown_vein", veinName.toString()));
        }

        // Search for veins matching type
        //noinspection EqualsBetweenInconvertibleTypes
        veins.removeIf(x -> !x.getType().equals(type));
        source.sendFeedback(new TranslationTextComponent(MOD_ID + ".command.veins_found"), true);
        for (Vein<?> vein : veins)
        {
            ITextComponent resultText = new TranslationTextComponent(MOD_ID + ".command.vein_info", vein.toString());
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
