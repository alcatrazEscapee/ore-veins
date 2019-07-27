/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */


package com.alcatrazescapee.oreveins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import net.minecraft.block.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;

import com.alcatrazescapee.oreveins.util.json.BlockStateDeserializer;

@SuppressWarnings("WeakerAccess")
public final class Config
{
    public static final ServerConfig COMMON;

    static final ForgeConfigSpec COMMON_SPEC;

    static
    {
        final Pair<ServerConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static final class ServerConfig
    {
        public final ForgeConfigSpec.BooleanValue noOres;
        public final ForgeConfigSpec.BooleanValue debugCommands;
        public final ForgeConfigSpec.IntValue extraChunkRange;
        public final ForgeConfigSpec.BooleanValue enableDefaultVeins;

        private final ForgeConfigSpec.ConfigValue<List<? extends String>> disabledOres;

        ServerConfig(ForgeConfigSpec.Builder builder)
        {
            builder.push("general");

            noOres = builder
                    .comment("Stop all vanilla ore gen calls? Warning: this includes calls such as andesite/diorite, and potentially others that internally behave the same as ores. For more customization, see the disabled ores option.")
                    .define("noOres", false);

            disabledOres = builder
                    .comment("Vanilla ore gen to disable. Must be specified as a list of block states, i.e. minecraft:gold_ore, minecraft:iron_ore, etc.")
                    .defineList("disabledOres", this::defaultDisabledBlockStates, obj -> BlockStateDeserializer.INSTANCE.isBlockState(obj.toString()));

            debugCommands = builder
                    .comment("Enable debug commands such as /veininfo, /clearworld, /findveins")
                    .define("debugCommands", true);

            extraChunkRange = builder
                    .comment("Extra chunk search range when generating veins", "Use if your veins are getting cut off at chunk boundaries")
                    .defineInRange("extraChunkRange", 0, 0, 20);

            enableDefaultVeins = builder
                    .comment("Enable veins in the default data pack. (This is the 'oreveins:default_veins' condition")
                    .define("enableDefaultVeins", true);

            builder.pop();
        }

        public Set<BlockState> disabledBlockStates()
        {
            return disabledOres.get().stream().map(BlockStateDeserializer.INSTANCE::readBlockState).collect(Collectors.toSet());
        }

        private List<? extends String> defaultDisabledBlockStates()
        {
            return new ArrayList<>(Arrays.asList("minecraft:coal_ore", "minecraft:iron_ore", "minecraft:gold_ore", "minecraft:diamond_ore", "minecraft:lapis_ore", "minecraft:redstone_ore", "minecraft:diamond_ore"));
        }
    }
}
