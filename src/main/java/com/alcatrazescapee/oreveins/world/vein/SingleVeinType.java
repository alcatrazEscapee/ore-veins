/*
 * Part of the Realistic Ore Veins Mod by AlcatrazEscapee
 * Work under Copyright. See the project LICENSE.md for details.
 */

package com.alcatrazescapee.oreveins.world.vein;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import com.alcatrazescapee.oreveins.util.collections.IWeightedList;

public abstract class SingleVeinType<V extends Vein<?>> extends VeinType<V>
{
    private final Predicate<BlockState> stoneStates;
    private final IWeightedList<BlockState> oreStates;

    protected SingleVeinType(JsonObject json, JsonDeserializationContext context) throws JsonParseException
    {
        super(json, context);

        if (!json.has("stone") || !json.has("ore"))
        {
            throw new JsonParseException("Single vein type must contain both 'stone' and 'ore' entries");
        }
        stoneStates = context.deserialize(json.get("stone"), new TypeToken<Predicate<BlockState>>() {}.getType());
        oreStates = context.deserialize(json.get("ore"), new TypeToken<IWeightedList<BlockState>>() {}.getType());
        if (oreStates.isEmpty())
        {
            throw new JsonParseException("Ore States cannot be empty.");
        }
    }

    public BlockState getStateToGenerate(V vein, BlockPos pos, Random random)
    {
        return oreStates.get(random);
    }

    public Collection<BlockState> getOreStates()
    {
        return oreStates.values();
    }

    @Override
    public boolean canGenerateAt(IBlockReader world, BlockPos pos)
    {
        BlockState stoneState = world.getBlockState(pos);
        return stoneStates.test(stoneState) && super.canGenerateAt(world, pos);
    }

    @Override
    public void createVeins(List<Vein<?>> veins, int chunkX, int chunkZ, Random random)
    {
        Vein<?> vein = createVein(chunkX, chunkZ, random);
        if (vein.getType().isValidPos(vein.getPos()))
        {
            veins.add(vein);
        }
    }

    public abstract V createVein(int chunkX, int chunkZ, Random random);

    protected final Vein<?> createDefaultVein(int chunkX, int chunkZ, Random random)
    {
        return new Vein<>(this, defaultStartPos(chunkX, chunkZ, random));
    }
}
