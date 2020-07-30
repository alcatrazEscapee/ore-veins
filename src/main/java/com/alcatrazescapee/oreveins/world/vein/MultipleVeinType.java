/*
 * Part of the Realistic Ore Veins Mod by AlcatrazEscapee
 * Work under Copyright. See the project LICENSE.md for details.
 */

package com.alcatrazescapee.oreveins.world.vein;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import net.minecraft.block.BlockState;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.BlockPos;

public class MultipleVeinType extends VeinType<Vein<?>>
{
    private final List<VeinType<?>> types;

    public MultipleVeinType(JsonObject json, JsonDeserializationContext context) throws JsonParseException
    {
        super(json, context);

        types = context.deserialize(JSONUtils.getJsonArray(json, "veins"), new TypeToken<List<VeinType<?>>>() {}.getType());
        if (types.size() < 2)
        {
            throw new IllegalStateException("Multiple vein must have at least two child veins!");
        }
    }

    @Override
    public BlockState getStateToGenerate(Vein<?> vein, BlockPos pos, Random random)
    {
        throw new IllegalStateException("This should never be called directly");
    }

    @Override
    public Collection<BlockState> getOreStates()
    {
        return types.stream().map(VeinType::getOreStates).flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Nullable
    @Override
    public Indicator getIndicator(Random random)
    {
        throw new IllegalStateException("This should never be called directly");
    }

    @Override
    public int getChunkRadius()
    {
        return types.stream().mapToInt(VeinType::getChunkRadius).max().orElse(0);
    }

    @Override
    public float getChanceToGenerate(Vein<?> vein, BlockPos pos)
    {
        throw new IllegalStateException("This should never be called directly");
    }

    @Override
    public void createVeins(List<Vein<?>> veins, int chunkX, int chunkZ, Random random)
    {
        BlockPos centerPos = defaultStartPos(chunkX, chunkZ, random);
        List<Vein<?>> innerVeins = new ArrayList<>();
        innerVeins.add(new MultipleVein(this, centerPos));
        for (VeinType<?> type : types)
        {
            type.createVeins(innerVeins, chunkX, chunkZ, random);
        }
        innerVeins.forEach(vein -> {
            vein.setPos(centerPos);
            veins.add(vein);
        });
    }

    public static class MultipleVein extends Vein<MultipleVeinType>
    {
        MultipleVein(MultipleVeinType type, BlockPos pos)
        {
            super(type, pos);
        }

        @Override
        public boolean inRange(int x, int z)
        {
            return false; // Never in range, so this vein should never generate in the world. It only serves as a marker for commands like /findveins
        }
    }
}
