/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.world.veins;

import java.util.*;
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
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

import com.alcatrazescapee.oreveins.api.IRule;
import com.alcatrazescapee.oreveins.api.IVeinType;
import com.alcatrazescapee.oreveins.commands.ClearWorldCommand;
import com.alcatrazescapee.oreveins.util.IWeightedList;
import com.alcatrazescapee.oreveins.util.json.BlockStateDeserializer;
import com.alcatrazescapee.oreveins.util.json.BlockStateListDeserializer;
import com.alcatrazescapee.oreveins.util.json.RuleDeserializer;
import com.alcatrazescapee.oreveins.util.json.WeightedListDeserializer;
import com.alcatrazescapee.oreveins.world.VeinsFeature;
import com.alcatrazescapee.oreveins.world.indicator.Indicator;

@ParametersAreNonnullByDefault
public class VeinManager extends JsonReloadListener
{
    public static final VeinManager INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(new TypeToken<IWeightedList<BlockState>>() {}.getType(), new WeightedListDeserializer<>(BlockState.class))
            .registerTypeAdapter(new TypeToken<IWeightedList<Indicator>>() {}.getType(), new WeightedListDeserializer<>(Indicator.class))
            .registerTypeAdapter(new TypeToken<List<BlockState>>() {}.getType(), BlockStateListDeserializer.INSTANCE)
            .registerTypeAdapter(BlockState.class, BlockStateDeserializer.INSTANCE)
            .registerTypeAdapter(IRule.class, RuleDeserializer.INSTANCE)
            .disableHtmlEscaping()
            .create();

    static
    {
        // Constructor call must come after
        INSTANCE = new VeinManager();
    }

    private final BiMap<ResourceLocation, IVeinType> veins;
    private final Map<String, Class<? extends IVeinType<?>>> veinTypes;

    private VeinManager()
    {
        super(GSON, "oreveins");

        this.veins = HashBiMap.create();
        this.veinTypes = new HashMap<>();

        veinTypes.put("sphere", SphereVeinType.class);
        veinTypes.put("cluster", ClusterVeinType.class);
        veinTypes.put("cone", ConeVeinType.class);
        veinTypes.put("pipe", PipeVeinType.class);
        veinTypes.put("curve", CurveVeinType.class);
    }

    @Nonnull
    public Collection<IVeinType> getVeins()
    {
        return veins.values();
    }

    @Nonnull
    public Set<ResourceLocation> getKeys()
    {
        return veins.keySet();
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

    @Nonnull
    @SuppressWarnings("unused")
    public Map<String, Class<? extends IVeinType<?>>> getVeinTypes()
    {
        return veinTypes;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonObject> resources, IResourceManager manager, IProfiler profiler)
    {
        for (Map.Entry<ResourceLocation, JsonObject> entry : resources.entrySet())
        {
            ResourceLocation name = entry.getKey();
            JsonObject json = entry.getValue();
            try
            {
                if (CraftingHelper.processConditions(json, "rules"))
                {
                    String veinType = JSONUtils.getString(json, "type");
                    if (!veinTypes.containsKey(veinType))
                    {
                        LOGGER.warn("Vein '{}' has an unknown type '{}'. Defaulting to 'sphere' type.", name, veinType);
                    }
                    IVeinType<?> vein = GSON.fromJson(json, veinTypes.getOrDefault(veinType, SphereVeinType.class));
                    if (vein.isValid())
                    {
                        veins.put(name, vein);
                    }
                    else
                    {
                        LOGGER.warn("Vein '{}' is invalid. This is likely caused by one or more required parameters being left out.", name);
                    }
                }
                else
                {
                    LOGGER.info("Skipping loading vein '{}' as it's rules were not met", name);
                }
            }
            catch (IllegalArgumentException | JsonParseException e)
            {
                LOGGER.warn("Vein '{}' failed to parse. This is most likely caused by incorrectly specified JSON.", entry.getKey());
                LOGGER.warn("Error: ", e);
            }
        }

        LOGGER.info("Registered {} Veins Successfully.", veins.size());

        // After Veins have Reloaded
        ClearWorldCommand.resetVeinStates();
        VeinsFeature.resetChunkRadius();
    }
}
