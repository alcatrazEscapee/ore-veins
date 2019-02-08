/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.world;

import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.alcatrazescapee.oreveins.OreVeins;
import com.alcatrazescapee.oreveins.OreVeinsConfig;

public class WorldGenReplacer
{
    @SubscribeEvent
    public void onGenerateMineable(OreGenEvent.GenerateMinable event)
    {
        if (OreVeinsConfig.NO_ORES || isBlockedType(event.getType()))
            event.setResult(Event.Result.DENY);
    }

    private boolean isBlockedType(OreGenEvent.GenerateMinable.EventType type)
    {
        for (String s : OreVeinsConfig.STOPPED_ORES)
        {
            try
            {
                if (OreGenEvent.GenerateMinable.EventType.valueOf(s.toUpperCase()) == type) return true;
            }
            catch (IllegalArgumentException e)
            {
                OreVeins.getLog().warn("Illegal type is specified in Ore Veins Config at STOPPED_ORES. Spelling error?");
                // Just skip this
            }
        }
        return false;
    }
}
