package com.alcatrazescapee.oreveins;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.ForgeRegistries;

import com.alcatrazescapee.oreveins.world.vein.*;
import org.junit.jupiter.api.Test;

import static com.alcatrazescapee.oreveins.MinecraftReferences.pos;
import static com.alcatrazescapee.oreveins.MinecraftReferences.world;
import static com.alcatrazescapee.oreveins.OreVeins.MOD_ID;
import static org.junit.jupiter.api.Assertions.*;

class TestVeins
{
    @Test
    void testBiomeRulesAnd()
    {
        VeinType<?> vein = VeinManager.INSTANCE.getVein(new ResourceLocation(MOD_ID, "tests/test_biome_rules_and"));

        assertNotNull(vein);
        for (Biome biome : ForgeRegistries.BIOMES.getValues())
        {
            assertEquals(BiomeDictionary.hasType(biome, BiomeDictionary.Type.HOT) && BiomeDictionary.hasType(biome, BiomeDictionary.Type.DRY), vein.matchesBiome(() -> biome)); // hot and dry
        }
    }

    @Test
    void testBiomeRulesNotNot()
    {
        VeinType<?> vein = VeinManager.INSTANCE.getVein(new ResourceLocation(MOD_ID, "tests/test_biome_rules_not_not"));

        assertNotNull(vein);
        for (Biome biome : ForgeRegistries.BIOMES.getValues())
        {
            assertEquals(biome == Biomes.PLAINS, vein.matchesBiome(() -> biome)); // not not plains
        }
    }

    @Test
    void testBiomeRulesNotOr()
    {
        VeinType<?> vein = VeinManager.INSTANCE.getVein(new ResourceLocation(MOD_ID, "tests/test_biome_rules_not_or"));

        assertNotNull(vein);
        for (Biome biome : ForgeRegistries.BIOMES.getValues())
        {
            assertEquals(!(biome == Biomes.PLAINS || biome == Biomes.FOREST), vein.matchesBiome(() -> biome)); // not (plains or forest)
        }
    }

    @Test
    void testBiomeRulesOr()
    {
        VeinType<?> vein = VeinManager.INSTANCE.getVein(new ResourceLocation(MOD_ID, "tests/test_biome_rules_or"));
        assertNotNull(vein);

        assertNotNull(vein);
        for (Biome biome : ForgeRegistries.BIOMES.getValues())
        {
            assertEquals(biome == Biomes.PLAINS || biome == Biomes.FOREST, vein.matchesBiome(() -> biome)); // plains or forest
        }
    }

    @Test
    void testBiomeRulesOrList()
    {
        VeinType<?> vein = VeinManager.INSTANCE.getVein(new ResourceLocation(MOD_ID, "tests/test_biome_rules_or_list"));

        assertNotNull(vein);
        for (Biome biome : ForgeRegistries.BIOMES.getValues())
        {
            assertEquals(biome == Biomes.PLAINS || biome == Biomes.FOREST, vein.matchesBiome(() -> biome)); // plains or forest
        }
    }

    @Test
    void testBiomesTagCold()
    {
        VeinType<?> vein = VeinManager.INSTANCE.getVein(new ResourceLocation(MOD_ID, "tests/test_biomes_tag_cold"));

        assertNotNull(vein);
        for (Biome biome : ForgeRegistries.BIOMES.getValues())
        {
            assertEquals(BiomeDictionary.hasType(biome, BiomeDictionary.Type.COLD), vein.matchesBiome(() -> biome));
        }
    }

    @Test
    void testCluster()
    {
        VeinType<?> vein = VeinManager.INSTANCE.getVein(new ResourceLocation(MOD_ID, "tests/test_cluster"));

        assertNotNull(vein);
        assertEquals(ClusterVeinType.class, vein.getClass());
    }

    @Test
    void testCone()
    {
        VeinType<?> vein = VeinManager.INSTANCE.getVein(new ResourceLocation(MOD_ID, "tests/test_cone"));

        assertNotNull(vein);
        assertEquals(ConeVeinType.class, vein.getClass());
    }

    @Test
    void testIndicatorIgnoreLiquids()
    {
        VeinType<?> vein = VeinManager.INSTANCE.getVein(new ResourceLocation(MOD_ID, "tests/test_indicator_ignore_liquids"));

        assertNotNull(vein);

        Indicator indicator = vein.getIndicator(new Random());
        assertNotNull(indicator);
        assertEquals(3, indicator.getRarity());
        assertTrue(indicator.shouldIgnoreLiquids());
        assertEquals(Blocks.RED_WOOL.getDefaultState(), indicator.getStateToGenerate(new Random()));
    }

    @Test
    void testIndicatorLiquids()
    {
        VeinType<?> vein = VeinManager.INSTANCE.getVein(new ResourceLocation(MOD_ID, "tests/test_indicator_liquids"));

        assertNotNull(vein);

        Indicator indicator = vein.getIndicator(new Random());
        assertNotNull(indicator);
        assertEquals(3, indicator.getRarity());
        assertFalse(indicator.shouldIgnoreLiquids());
        assertEquals(Blocks.BLACK_WOOL.getDefaultState(), indicator.getStateToGenerate(new Random()));
    }

    @Test
    void testMixedTagStoneBlocks()
    {
        VeinType<?> vein = VeinManager.INSTANCE.getVein(new ResourceLocation(MOD_ID, "tests/test_mixed_tag_stone_blocks"));

        assertNotNull(vein);
        world.setBlockState(pos, Blocks.STONE.getDefaultState());
        assertTrue(vein.canGenerateAt(world, pos));
        world.setBlockState(pos, Blocks.SAND.getDefaultState());
        assertTrue(vein.canGenerateAt(world, pos));
        world.setBlockState(pos, Blocks.GLASS.getDefaultState());
        assertFalse(vein.canGenerateAt(world, pos));
    }

    @Test
    void testMultipleOreBlocks()
    {
        VeinType<?> vein = VeinManager.INSTANCE.getVein(new ResourceLocation(MOD_ID, "tests/test_multiple_ore_blocks"));

        assertNotNull(vein);

        List<BlockState> ores = Arrays.asList(Blocks.CYAN_WOOL.getDefaultState(), Blocks.PURPLE_WOOL.getDefaultState());
        for (int i = 0; i < 10; i++)
        {
            assertTrue(ores.contains(vein.getStateToGenerate(new Random())));
        }
    }

    @Test
    void testMultipleStoneBlocks()
    {
        VeinType<?> vein = VeinManager.INSTANCE.getVein(new ResourceLocation(MOD_ID, "tests/test_multiple_stone_blocks"));

        assertNotNull(vein);
        world.setBlockState(pos, Blocks.STONE.getDefaultState());
        assertTrue(vein.canGenerateAt(world, pos));
        world.setBlockState(pos, Blocks.GRANITE.getDefaultState());
        assertTrue(vein.canGenerateAt(world, pos));
        world.setBlockState(pos, Blocks.GLASS.getDefaultState());
        assertFalse(vein.canGenerateAt(world, pos));
    }

    @Test
    void testMultipleTagStoneBlocks()
    {
        VeinType<?> vein = VeinManager.INSTANCE.getVein(new ResourceLocation(MOD_ID, "tests/test_multiple_tag_stone_blocks"));

        assertNotNull(vein);
        world.setBlockState(pos, Blocks.STONE.getDefaultState());
        assertTrue(vein.canGenerateAt(world, pos));
        world.setBlockState(pos, Blocks.SAND.getDefaultState());
        assertTrue(vein.canGenerateAt(world, pos));
        world.setBlockState(pos, Blocks.GLASS.getDefaultState());
        assertFalse(vein.canGenerateAt(world, pos));
    }

    @Test
    void testMultipleWeightedOreBlocks()
    {
        VeinType<?> vein = VeinManager.INSTANCE.getVein(new ResourceLocation(MOD_ID, "tests/test_multiple_weighted_ore_blocks"));

        assertNotNull(vein);

        Random random = new Random();
        List<BlockState> ores = Arrays.asList(Blocks.BLUE_WOOL.getDefaultState(), Blocks.BROWN_WOOL.getDefaultState());
        for (int i = 0; i < 10; i++)
        {
            assertTrue(ores.contains(vein.getStateToGenerate(random)));
        }
    }

    @Test
    void testPipe()
    {
        VeinType<?> vein = VeinManager.INSTANCE.getVein(new ResourceLocation(MOD_ID, "tests/test_pipe"));

        assertNotNull(vein);
        assertEquals(PipeVeinType.class, vein.getClass());
    }

    @Test
    void testPlainsBiome()
    {
        VeinType<?> vein = VeinManager.INSTANCE.getVein(new ResourceLocation(MOD_ID, "tests/test_plains_biome"));

        assertNotNull(vein);
        for (Biome biome : ForgeRegistries.BIOMES.getValues())
        {
            assertEquals(biome == Biomes.PLAINS, vein.matchesBiome(() -> biome));
        }
    }

    @Test
    void testSingleCluster()
    {
        VeinType<?> vein = VeinManager.INSTANCE.getVein(new ResourceLocation(MOD_ID, "tests/test_single_cluster"));

        assertNotNull(vein);
        assertEquals(ClusterVeinType.class, vein.getClass());
    }

    @Test
    void testSphere()
    {
        VeinType<?> vein = VeinManager.INSTANCE.getVein(new ResourceLocation(MOD_ID, "tests/test_sphere"));

        assertNotNull(vein);
        assertEquals(SphereVeinType.class, vein.getClass());
    }

    @Test
    void testTagStoneBlocks()
    {
        VeinType<?> vein = VeinManager.INSTANCE.getVein(new ResourceLocation(MOD_ID, "tests/test_tag_stone_blocks"));

        assertNotNull(vein);
        world.setBlockState(pos, Blocks.STONE.getDefaultState());
        assertTrue(vein.canGenerateAt(world, pos));
        world.setBlockState(pos, Blocks.GRANITE.getDefaultState());
        assertTrue(vein.canGenerateAt(world, pos));
        world.setBlockState(pos, Blocks.GLASS.getDefaultState());
        assertFalse(vein.canGenerateAt(world, pos));
    }

    @Test
    void testUniformSphere()
    {
        VeinType<?> vein = VeinManager.INSTANCE.getVein(new ResourceLocation(MOD_ID, "tests/test_tag_stone_blocks"));

        assertNotNull(vein);
        assertEquals(SphereVeinType.class, vein.getClass());
    }

    @Test
    void testDimensionRulesNotOr()
    {
        VeinType<?> vein = VeinManager.INSTANCE.getVein(new ResourceLocation(MOD_ID, "tests/test_dimension_rules_not_or"));

        assertNotNull(vein);
        assertFalse(vein.matchesDimension(DimensionType.OVERWORLD));
        assertFalse(vein.matchesDimension(DimensionType.THE_NETHER));
        assertTrue(vein.matchesDimension(DimensionType.THE_END));
    }

    @Test
    void testDimensionRulesNotOrArray()
    {
        VeinType<?> vein = VeinManager.INSTANCE.getVein(new ResourceLocation(MOD_ID, "tests/test_dimension_rules_not_or_array"));

        assertNotNull(vein);
        assertFalse(vein.matchesDimension(DimensionType.OVERWORLD));
        assertFalse(vein.matchesDimension(DimensionType.THE_NETHER));
        assertTrue(vein.matchesDimension(DimensionType.THE_END));
    }

    @Test
    void testDimensionRulesOr()
    {
        VeinType<?> vein = VeinManager.INSTANCE.getVein(new ResourceLocation(MOD_ID, "tests/test_dimension_rules_or"));

        assertNotNull(vein);
        assertTrue(vein.matchesDimension(DimensionType.OVERWORLD));
        assertTrue(vein.matchesDimension(DimensionType.THE_NETHER));
        assertFalse(vein.matchesDimension(DimensionType.THE_END));
    }
}
