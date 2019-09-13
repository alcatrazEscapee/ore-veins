/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.commands;

import java.util.concurrent.CompletableFuture;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import com.alcatrazescapee.oreveins.world.vein.VeinManager;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import static com.alcatrazescapee.oreveins.OreVeins.MOD_ID;

@ParametersAreNonnullByDefault
public class VeinTypeArgument implements ArgumentType<ResourceLocation>
{
    private static final DynamicCommandExceptionType VEIN_NOT_FOUND = new DynamicCommandExceptionType(args -> new TranslationTextComponent(MOD_ID + ".tooltip.unknown_vein", args));


    static ResourceLocation getVein(CommandContext<?> context, @SuppressWarnings("SameParameterValue") String name)
    {
        return context.getArgument(name, ResourceLocation.class);
    }

    @Override
    public ResourceLocation parse(StringReader reader) throws CommandSyntaxException
    {
        ResourceLocation loc = ResourceLocation.read(reader);
        if (!VeinManager.INSTANCE.getKeys().contains(loc))
        {
            throw VEIN_NOT_FOUND.createWithContext(reader, loc.toString());
        }
        return loc;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        StringReader reader = new StringReader(builder.getInput());
        ISuggestionProvider.suggestIterable(VeinManager.INSTANCE.getKeys(), builder.createOffset(reader.getCursor()));
        return builder.buildFuture();
    }
}
