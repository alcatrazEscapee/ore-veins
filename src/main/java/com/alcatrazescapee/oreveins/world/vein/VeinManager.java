/*
 * Part of the Realistic Ore Veins Mod by AlcatrazEscapee
 * Work under Copyright. See the project LICENSE.md for details.
 */

package com.alcatrazescapee.oreveins.world.vein;

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
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

import com.alcatrazescapee.oreveins.commands.ClearWorldCommand;
import com.alcatrazescapee.oreveins.util.IWeightedList;
import com.alcatrazescapee.oreveins.util.json.BlockStateDeserializer;
import com.alcatrazescapee.oreveins.util.json.LenientListDeserializer;
import com.alcatrazescapee.oreveins.util.json.VeinTypeDeserializer;
import com.alcatrazescapee.oreveins.util.json.WeightedListDeserializer;
import com.alcatrazescapee.oreveins.world.VeinsFeature;
import com.alcatrazescapee.oreveins.world.rule.BiomeRule;
import com.alcatrazescapee.oreveins.world.rule.DimensionRule;
import com.alcatrazescapee.oreveins.world.rule.IRule;

@ParametersAreNonnullByDefault
public class VeinManager extends JsonReloadListener
{
    public static final VeinManager INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder()
            // Collections
            .registerTypeAdapter(new TypeToken<IWeightedList<BlockState>>() {}.getType(), new WeightedListDeserializer<>(BlockState.class))
            .registerTypeAdapter(new TypeToken<IWeightedList<Indicator>>() {}.getType(), new WeightedListDeserializer<>(Indicator.class))
            .registerTypeAdapter(new TypeToken<List<BlockState>>() {}.getType(), new LenientListDeserializer<>(BlockState.class, Collections::singletonList, ArrayList::new))
            .registerTypeAdapter(BlockState.class, BlockStateDeserializer.INSTANCE)
            .registerTypeAdapter(IRule.class, IRule.Deserializer.INSTANCE)
            .registerTypeAdapter(Indicator.class, Indicator.Deserializer.INSTANCE)
            .registerTypeAdapter(BiomeRule.class, BiomeRule.Deserializer.INSTANCE)
            .registerTypeAdapter(DimensionRule.class, DimensionRule.Deserializer.INSTANCE)
            .registerTypeAdapter(VeinType.class, VeinTypeDeserializer.INSTANCE)
            .disableHtmlEscaping()
            .create();

    static
    {
        // Constructor call must come after GSON declaration
        INSTANCE = new VeinManager();
    }

    private final BiMap<ResourceLocation, VeinType<?>> veins;
    private final Map<String, Class<? extends VeinType<?>>> veinTypes;

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
    public Collection<VeinType<?>> getVeins()
    {
        return veins.values();
    }

    @Nonnull
    public Set<ResourceLocation> getKeys()
    {
        return veins.keySet();
    }

    @Nullable
    public VeinType<?> getVein(ResourceLocation key)
    {
        return veins.get(key);
    }

    @Nonnull
    public ResourceLocation getName(VeinType<?> key)
    {
        return veins.inverse().get(key);
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
                if (CraftingHelper.processConditions(json, "conditions"))
                {
                    veins.put(name, GSON.fromJson(json, VeinType.class));
                }
                else
                {
                    LOGGER.info("Skipping loading vein '{}' as it's conditions were not met", name);
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
