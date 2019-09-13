/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nonnull;

public interface IWeightedList<E> extends Iterable<E>
{
    static <E> IWeightedList<E> empty()
    {
        return new IWeightedList<E>()
        {
            @Override
            public void add(double weight, E element) {}

            @Override
            public E get(Random random)
            {
                return null;
            }

            @Override
            public Collection<E> values()
            {
                return Collections.emptyList();
            }

            @Override
            public boolean isEmpty()
            {
                return true;
            }

            @Override
            @Nonnull
            public Iterator<E> iterator()
            {
                return Collections.emptyIterator();
            }
        };
    }

    static <E> IWeightedList<E> singleton(E element)
    {
        return new IWeightedList<E>()
        {
            private Collection<E> elementSet = Collections.singleton(element);

            @Override
            public void add(double weight, E element) {}

            @Override
            public E get(Random random)
            {
                return element;
            }

            @Override
            public Collection<E> values()
            {
                return elementSet;
            }

            @Override
            public boolean isEmpty()
            {
                return false;
            }

            @Override
            @Nonnull
            public Iterator<E> iterator()
            {
                return elementSet.iterator();
            }

            @Override
            public String toString()
            {
                return "[" + element + "]";
            }
        };
    }

    void add(double weight, E element);

    E get(Random random);

    Collection<E> values();

    boolean isEmpty();
}
