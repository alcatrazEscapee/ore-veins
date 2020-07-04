package com.alcatrazescapee.oreveins;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import org.junit.jupiter.api.Test;

import static com.alcatrazescapee.oreveins.OreVeins.MOD_ID;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SuppressWarnings("WeakerAccess")
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MinecraftReferences
{
    public static MinecraftServer server = null;
    public static World world = null;
    public static BlockPos pos = new BlockPos(0, 60, 0);

    @SubscribeEvent
    public static void onServerStarting(FMLServerStartingEvent event)
    {
        server = event.getServer();
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event)
    {
        if (event.getWorld().getDimension().getType() == DimensionType.OVERWORLD)
        {
            world = (World) event.getWorld();
        }
    }

    @Test
    void testGotServer()
    {
        assertNotNull(server);
    }

    @Test
    void testGotWorld()
    {
        assertNotNull(world);
    }
}
