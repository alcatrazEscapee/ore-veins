![Ore Veins Banner Image](https://github.com/alcatrazEscapee/ore-veins/blob/1.13/img/banner.png?raw=true)

This is a Minecraft mod to add realistically shaped veins of ore to your world. Useful for custom maps, mod packs, or just a different survival experience. Everything is fully configurable via json, meaning you can have ore veins of whatever type of shape or size you want.

For example images of various types and configurations of veins see the Curseforge images [here](https://minecraft.curseforge.com/projects/realistic-ore-veins/images).

Despite the wide array of configuration options available, if you desire additional vein types, rules, or features, please submit a feature request on the issue tracker, and I'll be happy to investigate.

## Configuration

**This has changed in 1.14!**

Veins are now loaded from datapacks. This has several advantages:

 - Veins can now by easily affected by recipe conditions (i.e. enabling or disabling the vein based on various factors)
 - Veins can be reloaded during play when reloading all data packs
 - Configuration for veins can be present on the server only

Each vein must be a **separate** json file, located under the path `data/domain/oreveins/`. For instance, Realistic Ore Veins includes several default ore veins, which are found under `data/oreveins/oreveins/default_???.json`. (The first `oreveins` is the path required for the veins, the second is the domain of this mod / datapack)

A vein must consist of a single json object, which contains at least the following entries:

* `type` is the registry name of the [Vein Type](#vein-types) that this entry will spawn. Based on what vein this is, there might be other required or optional values as well.
* `stone` is a [Block Entry](#block-entries). This represents the block states that the ore can spawn in.
* `ore` is a [Block Entry](#block-entries), with optional weights. This represents the possible states that the vein will spawn. This **does** support weighted entries.

Each entry can also contain any or all of the following values. If they don't exist, they will assume a default value. These apply to all vein types:

* `count` (Default: 1) Generate at most N veins per chunk. Rarity is applied to each attempt to generate a vein.
* `rarity` (Default: 10) 1 / N chunks will spawn this ore vein.
* `min_y` (Default: 16) Minimum y value for veins to generate at.
* `max_y` (Default: 64) Maximum y value for veins to generate at.
* `density` (Default: 50) Density of the ore vein. Higher values are more dense. (Tip: This number is not a percentage. For 100% density use values >1000)
* `vertical_size` (Default: 15) Vertical radius. This is not an absolute number in blocks, but is close to. Experimentation is required.
* `horizontal_size` (Default: 8) Horizontal radius. This is not an absolute number in blocks, but is close to. Experimentation is required.
* `biomes` (Default: all) Whitelist of biome names or biome tags for a biome to spawn in. Must be a list of strings. For info on possible tags see the Forge [Biome Dictionary](https://github.com/MinecraftForge/MinecraftForge/blob/1.13.x/src/main/java/net/minecraftforge/common/BiomeDictionary.java).
* `biomes_is_whitelist` (Default: true) When false, the biome list becomes a blacklist
* `dimensions` (Default: `["overworld"]`) Whitelist of dimension names that the ore can spawn in. Must be a list of strings.
* `dimensions_is_whitelist` (Default: `true`) When false, the dimension list becomes a blacklist
* `indicator` (Default: `{}`) This is an [Indicator](#indicators) which will spawn on the surface underneath where the vein is found.
* `rules` (Default: `[]`) This is a list of [Rules](#rules) which are checked for each ore block that attempts to spawn. Must be a list of json objects, where each object is a rule
* `conditions` (Default: `[]`) These are conditions that will enable or disable the vein. Realistic Ore Veins includes a condition `oreveins:default_veins`, which will disable the default veins if the relevant config value is set. For more information on conditions, consult the minecraft wiki

### Vein Types

Vein types represent different types of shapes or structures that can be generated. A vein type must be specified for each vein.

##### Sphere: `"type": "sphere"`
This represents a single sphere (or spheroid, if vertical and horizontal size values are different). This vein type has no additional parameters.

##### Clusters: `"type": "cluster"`
This vein represents a scattered group of spheroids.  This vein type has an optional parameter:
* `clusters` (Default: 3) This represents the average number of other clusters that will spawn as part of this vein.

##### Vertical Pipe: `"type": "pipe"`
This vein represents a single vertical column / cylinder. This vein type has no additional parameters.

##### Cone: `"type": "cone"`
This vein represents a vertical cone. The pointy end of the cone can point upwards or downwards. This vein type has two optional parameters:
* `inverted` (Default: false) If true, the cone will have a pointy end facing down. If false, the pointy end will face up
* `shape` (Default: 0.5) This value determines how pointy the cone will be. It should be between 0.0 and 1.0. Higher values mean less pointy (more cylindrical). Smaller values are more pointy

##### Curve: `"type": "curve"`
This vein represents a curve (created with a cubic Bezier curve.) It has two optional parameters:
* `radius` (Default: 5) This is the approximate radius of the curve in blocks.
* `angle` (Default: 45) This is the maximum angle for vertical vein rotation, in a range from 0 to 90. Zero be completely horizontal, and 90 will have the full range of vertical directions to curve in.


### Rules

Rules are conditions that are checked for each individual ore block. This allows ore blocks to only spawn on cave walls for example, by defining a rule that only spawns ores if it is touching an air block.

A rule consists of a json object, which contains at least the following entries:

 - `type`: This is the type of the rule. Additional rules may have other required entries depending on the rule.

##### Touching: `"type": "touching"`
This rule indicates that each ore block must touch another block. Touching is defined as the adjacent block on any of the cardinal directions, up, or down. It has one required parameter:
* `block`: This is the block that the vein will be touching.

This rule also has the following optional parameters:
* `min` (Default: 1) This is the minimum number (inclusive) of the block that the ore must touch. Must be an integer.
* `max` (Default: 6) This is the maximum number (inclusive) of the block that the ore must touch. Must be an integer.


### Indicators

Indicators are configurable objects that will spawn on the surface when a vein is detected underneath them. An indicator must contain the following entries:

* `blocks` is a [Block Entry](#block-entries). This represents the possible states that the indicator will spawn. This supports weighted entries.

Indicators can also contain the following optional entries

* `rarity` (Default: 10) 1 / N blocks will generate an indicator, provided there is a valid ore block directly underneath.
* `max_depth` (Default: 32) This is the maximum depth for an ore block to generate which would attempt to spawn an indicator.
* `ignore_vegetation` (Default: true) If the vein should ignore vegetation when trying to spawn indicators. (i.e. should the indicators spawn underneath trees, leaves or huge mushrooms?)
* `ignore_liquids` (Default: false) If the vein should ignore liquids when trying to spawn indicators. (i.e. should the indicator spawn inside lakes or the ocean?)
* `blocks_under` (Default: accepts all blocks) This is a [Block Entry](#block-entries). The list of blocks that this indicator is allowed to spawn on.

An example indicator that spawns roses when ore blocks are less than twenty blocks under the surface would be added to the ore entry as such:

```json
{
  "type": "cluster",
  "stone": "minecraft:stone",
  "ore": "minecraft:iron_ore",
  "indicator": {
    "blocks": "minecraft:red_flower",
    "max_depth": 20
  }
}
```

### Block Entries

**1.13 Removed Metadata - You can no longer specify blocks via their meta values.**

A Block Entry can be any of the following:

1. A single string representing a block's registry name: `"ore": "minecraft:iron_ore"`
2. A single object representing a block with various properties. Each property must be specified as a key-value pair. If left out, properties will assume their default value.
```json
{
  "block": "minecraft:oak_stairs",
  "half": "top",
  "facing": "east"
}
```
3. A list of objects (as above). Note that these can be weighed (when used in `ore`) but are not necessary. If weight is not found for a particular object, it will default to 1. Properties are optional.
```json
{
  "ore": [
   {
      "block": "minecraft:oak_stairs",
      "half": "top",
      "weight": 12
    },
    {
      "block": "minecraft:coal_ore",
      "weight": 39
    },
    {
      "block": "minecraft:diamond_ore"
    }
  ]
}
```
