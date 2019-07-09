/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

import com.alcatrazescapee.oreveins.api.IVeinType;
import com.alcatrazescapee.oreveins.command.ClearWorldCommand;
import com.alcatrazescapee.oreveins.util.json.BlockStateDeserializer;
import com.alcatrazescapee.oreveins.util.json.BlockStateListDeserializer;
import com.alcatrazescapee.oreveins.util.json.VeinTypeDeserializer;
import com.alcatrazescapee.oreveins.util.json.WeightedListDeserializer;
import com.alcatrazescapee.oreveins.world.VeinsFeature;

@ParametersAreNonnullByDefault
public class VeinReloadListener extends JsonReloadListener
{
    public static final VeinReloadListener INSTANCE = new VeinReloadListener();

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(IVeinType.class, new VeinTypeDeserializer())
            .registerTypeAdapter(new TypeToken<List<BlockState>>() {}.getType(), new BlockStateListDeserializer())
            .registerTypeAdapter(IWeightedList.class, new WeightedListDeserializer())
            .registerTypeAdapter(BlockState.class, new BlockStateDeserializer())
            .create();

    private final BiMap<ResourceLocation, IVeinType> veins;

    private VeinReloadListener()
    {
        super(GSON, "oreveins");

        this.veins = HashBiMap.create();
    }

    @Nonnull
    public Collection<IVeinType> getVeins()
    {
        return veins.values();
    }

    @Nullable
    public IVeinType getVein(ResourceLocation key)
    {
        return veins.get(key);
    }

    @Nonnull
    public ResourceLocation getName(IVeinType key)
    {
        return veins.inverse().get(key);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonObject> resources, IResourceManager manager, IProfiler profiler)
    {
        for (Map.Entry<ResourceLocation, JsonObject> entry : resources.entrySet())
        {
            ResourceLocation name = entry.getKey();
            try
            {
                if (!CraftingHelper.processConditions(entry.getValue(), "conditions"))
                {
                    LOGGER.info("Skipping loading vein {} as it's conditions were not met", name);
                    continue;
                }
                IVeinType<?> vein = GSON.fromJson(entry.getValue(), IVeinType.class);
                if (vein.isValid())
                {
                    veins.put(entry.getKey(), vein);
                }
                else
                {
                    LOGGER.warn("Vein {} is invalid. This is likely caused by one or more required parameters being left out.", entry.getKey());
                }
            }
            catch (IllegalArgumentException | JsonParseException e)
            {
                LOGGER.warn("Vein {} failed to parse. This is most likely caused by incorrectly specified JSON.", entry.getKey());
                LOGGER.warn("Error: ", e);
            }
        }

        LOGGER.info("Registered {} Veins Successfully.", veins.size());

        // After Veins have Reloaded
        ClearWorldCommand.resetVeinStates(this);
        VeinsFeature.resetChunkRadius(this);
    }
}
