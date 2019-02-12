/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.util;

import java.util.*;
import javax.annotation.Nonnull;

public class WeightedList<E> implements IWeightedList<E>
{
    private double totalWeight;
    private NavigableMap<Double, E> map;

    public WeightedList()
    {
        this.totalWeight = 0;
        this.map = new TreeMap<>();
    }

    public void add(double weight, E element)
    {
        if (weight > 0)
        {
            totalWeight += weight;
            map.put(totalWeight, element);
        }
    }

    public E get(Random random)
    {
        double value = random.nextDouble() * totalWeight;
        return map.higherEntry(value).getValue();
    }

    @Override
    public Collection<E> values()
    {
        return map.values();
    }

    @Override
    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    @Override
    public String toString()
    {
        return map.toString();
    }

    @Override
    @Nonnull
    public Iterator<E> iterator()
    {
        return map.values().iterator();
    }
}
