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
        if (shouldBlock(event))
        {
            event.setResult(Event.Result.DENY);
        }
    }

    private boolean shouldBlock(OreGenEvent.GenerateMinable event)
    {
        int dimensionId = event.getWorld().provider.getDimension();
        boolean found = false;
        for (int configDimId : OreVeinsConfig.STOPPED_ORES_DIMENSIONS)
        {
            if (dimensionId == configDimId)
            {
                found = true;
                break;
            }
        }
        // Don't block ores if whitelisted + not found, or blacklisted + found
        if (OreVeinsConfig.STOPPED_ORES_DIMENSIONS_IS_WHITELIST != found)
        {
            return false;
        }
        if (OreVeinsConfig.NO_ORES)
        {
            // Block everything
            return true;
        }
        // Check by type
        OreGenEvent.GenerateMinable.EventType type = event.getType();
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
