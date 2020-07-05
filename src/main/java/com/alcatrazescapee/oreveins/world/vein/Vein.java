/*
 * Part of the Realistic Ore Veins Mod by AlcatrazEscapee
 * Work under Copyright. See the project LICENSE.md for details.
 */

package com.alcatrazescapee.oreveins.world.vein;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class Vein<T extends VeinType<?>>
{
    protected final T type;
    protected BlockPos pos;

    public Vein(T type, BlockPos pos)
    {
        this.pos = pos;
        this.type = type;
    }

    /**
     * Gets the centre position of this vein
     */
    public BlockPos getPos()
    {
        return pos;
    }

    /**
     * Sets the center pos of this vein
     */
    public void setPos(BlockPos pos)
    {
        this.pos = pos;
    }

    /**
     * Gets the type of this vein instance
     */
    public T getType()
    {
        return type;
    }

    @Override
    public String toString()
    {
        return String.format("Vein: %s, Pos: %s", VeinManager.INSTANCE.getName(type), pos);
    }

    /**
     * Checks if the vein is in range of a point.
     * This should typically call {@code getType().inRange()}
     *
     * @param x absolute x position
     * @param z absolute z position
     * @return if the vein can generate at any y position in that column
     */
    public boolean inRange(int x, int z)
    {
        return getTypeRaw().inRange(this, pos.getX() - x, pos.getZ() - z);
    }

    /**
     * Gets the chance to generate at a position
     * This should typically call {@code getType().getChanceToGenerate()}
     *
     * @param pos a position to generate at
     * @return a chance, with <= 0 meaning no chance, >= 1 indicating 100% chance
     */
    public float getChanceToGenerate(BlockPos pos)
    {
        return getTypeRaw().getChanceToGenerate(this, pos);
    }

    public BlockState getStateToGenerate(BlockPos pos, Random random)
    {
        return getTypeRaw().getStateToGenerate(this, pos, random);
    }

    @SuppressWarnings("unchecked")
    private <V extends Vein<T>> VeinType<V> getTypeRaw()
    {
        return ((VeinType<V>) type);
    }
}
