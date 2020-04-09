package com.alcatrazescapee.oreveins.world.rule;

import java.lang.reflect.Type;
import java.util.function.Predicate;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.*;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.BlockPos;

@ParametersAreNonnullByDefault
public class DistanceRule implements Predicate<BlockPos>
{
    public static final Predicate<BlockPos> DEFAULT = pos -> true;

    private final int minDistance;
    private final int maxDistance;
    private final int originX;
    private final int originZ;
    private final boolean useManhattanDistance;

    private DistanceRule(int minDistance, int maxDistance, int originX, int originZ, boolean useManhattanDistance)
    {
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.originX = originX;
        this.originZ = originZ;
        this.useManhattanDistance = useManhattanDistance;
    }

    @Override
    public boolean test(BlockPos pos)
    {
        int distance = getDistance(pos.getX(), pos.getZ());
        return distance > minDistance && distance < maxDistance;
    }

    private int getDistance(int x, int z)
    {
        int dx = Math.abs(x - originX), dz = Math.abs(z - originZ);
        if (!useManhattanDistance)
        {
            dx *= dx;
            dz *= dz;
        }
        return dx + dz;
    }

    public enum Deserializer implements JsonDeserializer<DistanceRule>
    {
        INSTANCE;

        @Override
        public DistanceRule deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            JsonObject json = JSONUtils.getJsonObject(jsonElement, "origin_distance");
            int minDistance = JSONUtils.getInt(json, "minimum_distance", 0);
            int maxDistance = JSONUtils.getInt(json, "maximum_distance", Integer.MAX_VALUE);
            int originX = JSONUtils.getInt(json, "origin_x", 0);
            int originZ = JSONUtils.getInt(json, "origin_z", 0);
            boolean useManhattanDistance = JSONUtils.getBoolean(json, "use_manhattan_distance", false);
            if (!useManhattanDistance)
            {
                // Square all distances, instead of comparing with square roots later
                minDistance *= minDistance;
                maxDistance *= maxDistance;
            }
            return new DistanceRule(minDistance, maxDistance, originX, originZ, useManhattanDistance);
        }
    }
}
