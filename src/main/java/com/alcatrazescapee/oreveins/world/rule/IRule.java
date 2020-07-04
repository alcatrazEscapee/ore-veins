/*
 * Part of the Realistic Ore Veins Mod by AlcatrazEscapee
 * Work under Copyright. See the project LICENSE.md for details.
 */

package com.alcatrazescapee.oreveins.world.rule;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

import com.google.gson.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public interface IRule extends BiPredicate<IBlockReader, BlockPos>
{
    @Override
    boolean test(IBlockReader world, BlockPos pos);

    enum Deserializer implements JsonDeserializer<IRule>
    {
        INSTANCE;

        public final Map<String, Factory<? extends IRule>> types = new HashMap<>();

        Deserializer()
        {
            types.put("touching", new TouchingRule.Factory());
        }

        @Override
        public IRule deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            JsonObject obj = json.getAsJsonObject();
            String conditionType = obj.get("type").getAsString();
            if (types.containsKey(conditionType))
            {
                return types.get(conditionType).parse(obj, context);
            }
            throw new JsonParseException("Unknown rule type: " + conditionType);
        }
    }

    interface Factory<T extends IRule>
    {
        T parse(JsonObject json, JsonDeserializationContext context);
    }
}
