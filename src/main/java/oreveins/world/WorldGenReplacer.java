package oreveins.world;

import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import oreveins.GenConfig;
import static oreveins.OreVeins.log;

public class WorldGenReplacer {

    @SubscribeEvent
    public void onGenerateMineable(OreGenEvent.GenerateMinable event) {
        if (event.getWorld().provider.getDimension() == 0) {
            if(GenConfig.NO_ORES || isBlockedType(event.getType()))
                event.setResult(Event.Result.DENY);
        }
    }

    private boolean isBlockedType(OreGenEvent.GenerateMinable.EventType type){
        for(String s : GenConfig.STOPPED_ORES){
            try {
                if (OreGenEvent.GenerateMinable.EventType.valueOf(s.toUpperCase()) == type) return true;
            }catch(IllegalArgumentException e){
                log.warn("Illegal type is specified in Ore Veins Config at STOPPED_ORES. Spelling error?");
                // Just skip this
            }
        }
        return false;
    }
}
