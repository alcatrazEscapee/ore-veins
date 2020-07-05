# Realistic Ore Veins

![Curseforge Build + Upload](https://github.com/alcatrazEscapee/ore-veins/workflows/Curseforge%20Build%20+%20Upload/badge.svg)

![Ore Veins Banner Image](https://github.com/alcatrazEscapee/ore-veins/blob/1.13/img/banner.png?raw=true)

This is a Minecraft mod to add realistically shaped veins of ore to your world. Useful for custom maps, mod packs, or just a different survival experience. Everything is fully configurable via json, meaning you can have ore veins of whatever type of shape or size you want.

For example images of various types and configurations of veins see the Curseforge images [here](https://minecraft.curseforge.com/projects/realistic-ore-veins/images).

Despite the wide array of configuration options available, if you desire additional vein types, rules, or features, please submit a feature request on the issue tracker, and I'll be happy to investigate.

## Configuration

**This is different than it was in 1.12!**

Veins are loaded from datapacks. This has several advantages:

 - Veins can now by easily affected by recipe conditions (i.e. enabling or disabling the vein based on various factors)
 - Veins can be reloaded during play when reloading all data packs, allowing changes to be made without restarting minecraft or the world.
 - Configuration for veins can be present on the server only.

Each vein must be a **separate** json file, located under the path `data/[namespace]/oreveins`, where `namespace` is the namespace of your mod or data pack. You should **not** be using either the `minecraft` or `oreveins` namespace! 

---

### Veins

Each vein must contain at least one entry, which identifies what kind of vein it is:

* `type` is an identifier of the [Vein Type](#vein-types) that this entry will spawn. Based on what vein this is, there might be other required or optional values as well.

There are two main types of vein types: **Single**, and **Multiple** types. Any **Single** vein type must contain the following entries. Except for `"type": "multiple"`, every vein type is a **Single** vein type, and thus must contain:

* `stone` is a [Block Entry](#block-entries). This represents the block states that the ore can spawn in.
* `ore` is a [Block Entry](#block-entries), with optional weights. This represents the possible states that the vein will spawn. This **does** support weighted entries.

```json
{
  "type": "sphere",
  "stone": "minecraft:stone",
  "ore": "minecraft:iron_ore"
}
```

Each entry can also contain any or all of the following values. If they don't exist, they will assume a default value. These apply to all vein types:

* `count` (Default: 1) Generate at most N veins per chunk. Rarity is applied to each attempt to generate a vein.
* `rarity` (Default: 10) 1 / N chunks will spawn this ore vein.
* `min_y` (Default: 16) Minimum y value for veins to generate at.
* `max_y` (Default: 64) Maximum y value for veins to generate at.
* `density` (Default: 50) Density of the ore vein. Higher values are more dense. (Tip: This number is not a percentage. For 100% density use values >1000)
* `vertical_size` (Default: 15) Vertical radius. This is not an absolute number in blocks, but is close to. Experimentation is required.
* `horizontal_size` (Default: 8) Horizontal radius. This is not an absolute number in blocks, but is close to. Experimentation is required.
* `biomes` (Default: Allow any) This is a [Biome Rule](#biome--dimension-rules). It specifies a list of biomes to include or exclude, or tags to include or exclude.
* `dimensions` (Default: Only Overworld) This is a [Dimension Rule](#biome--dimension-rules). It specifies a list of dimensions to include or exclude.
* `origin_distance` (Default: None). This is a [Distance Rule](#distance-rules). It specifies that a vein must satisfy a distance from an origin point, typically (0, 0)
* `indicator` (Default: None) This is an [Indicator](#indicators) which will spawn on the surface underneath where the vein is found.
* `rules` (Default: None) This is a list of [Spawn Rules](#spawn-rules) which are checked for each ore block that attempts to spawn. Must be a list of json objects, where each object is a rule.
* `conditions` (Default: None) These are conditions that will enable or disable the vein. For more information on conditions, consult the minecraft wiki.


The **Multiple** vein type allows you to group a series of other vein types to all spawn at the exact same location. For example, nested spheres made of different materials, or denser veins inside larger, more sparse veins. This requires the following entry:

* `veins` is a JSON array of [Vein Type](#vein-types) objects. When this vein spawns, each individual vein type specified here will create a vein according to the rules given by the individual vein type. As all of the optional fields above can be present both in the parent vein and the children veins, different fields will be ignored or used:
  * `count` and `rarity`, are only applied from the parent vein. When the parent vein spawns, each child vein will also spawn.
  * `min_y` and `max_y` can be used by both the parent and child veins, however the parent vein's y values will determine where the center of the vein is located, although the child vein will determine the range at which the vein can generate ores.
  * `density`, `vertical_size` and `horizontal_size` of the parent vein is completely ignored.
  
  


---

### Vein Types

Vein types represent different types of shapes or structures that can be generated. A vein type must be specified for each vein.

##### Sphere: `"type": "sphere"`
This represents a single sphere (or spheroid, if vertical and horizontal size values are different). This vein type has one optional parameter:

* `uniform` (Default: `false`). This is a boolean which determines if the density of the sphere will be uniformly distributed, or if it will be denser towards the center.

##### Clusters: `"type": "cluster"`
This vein represents a scattered group of spheroids. This vein type has an optional parameter:

* `clusters` (Default: 3) This represents the average number of other clusters that will spawn as part of this vein. This must be a strictly positive integer. Setting this to zero makes this the same as a sphere vein.

##### Vertical Pipe: `"type": "pipe"`
This vein represents a single vertical column / cylinder. This vein type has no additional parameters.

##### Cone: `"type": "cone"`
This vein represents a vertical cone. The pointy end of the cone can point upwards or downwards. This vein type has two optional parameters:

* `inverted` (Default: false) If true, the cone will have a pointy end facing down. If false, the pointy end will face up.
* `shape` (Default: 0.5) This value determines how pointy the cone will be. It should be between 0.0 and 1.0. Higher values mean less pointy (more cylindrical). Smaller values are more pointy.

##### Curve: `"type": "curve"`
This vein represents a curve (created with a cubic Bezier curve.) It has two optional parameters:
* `radius` (Default: 5) This is the approximate radius of the curve in blocks.
* `angle` (Default: 45) This is the maximum angle for vertical vein rotation, in a range from 0 to 90. Zero be completely horizontal, and 90 will have the full range of vertical directions to curve in.

---

### Biome / Dimension Rules

Biome and Dimension rules both can be a string, an array, or an object. The syntax is very similar for both*, except replacing the keyword `biomes` for the keyword `dimensions`. As such, all examples here will be of biome rules, but the logic is the same for using dimension rules.

\*One exception is that biomes have the `"type": "tag"` ability, which dimensions do not.

A biome / dimension rule entry can be a single string (which will match the single id provided), or it can be a json object, or it can be a list of objects (which will function the same as the `or` type below). If it is an object, it requires a `type` property. Valid types are:

 - `tag`: [Biomes only] Matches any tags found under `biomes`, which can be a string or string list
 - `and`: Matches all conditions found within the `biomes` sub-entry. `biomes` must be a list of conditions to match.
 - `or`: Matches any condition found within the `biomes` sub-entry. `biomes` must be a list of conditions to match.
 - `not`: Inverts the condition found within the `biomes` sub-entry.

The Simplest Example:
```json
"biomes": "minecraft:forest"
```
This will match the biome with the name `minecraft:forest`. Alternatively, a biome dictionary tag can be specified. In order to specify a tag, the type field `tag` is required:
```json
"biomes": { "type":  "tag", "biomes": "forest" }
```
This will match any biome that has the biome dictionary tag `forest`.

You can use an array as a logical "OR" operation, which will match any of the following entries:
```json
"biomes": [ 
  "minecraft:forest", 
  "minecraft:plains", 
  { "type": "tag", "biomes": "hot" }
]
```
The above will match a forest biome, a plains biome, or any hot biome.

There are also types for creating logical AND or NOT conditions:
```json
"biomes": {
  "type": "not",
  "biomes": [
    "minecraft:forest",
    "minecraft:desert"
  ]
}
```
This will act as a blacklist - if the biome is NOT a forest, NOR a desert, the vein will spawn. Similar with the AND:

```json
"biomes": {
  "type": "and",
  "biomes": [
    { "type": "tag", "biomes": "hot" },
    { "type": "tag", "biomes": "dry" }
  ]
}
```
This will only match biomes that are hot AND dry.

---

### Distance Rules

A distance rule consists of a single JSON object. It has no required fields, and four optional fields:

* `minimum_distance` (Default: 0). This is the minimum distance from the origin that is required for a vein to spawn.
* `maximum_distance` (Default: Max Int = 2,147,483,647). This is the maximum distance from the origin that is required for a vein to spawn.
* `origin_x` (Default: 0) This is the origin x position that is used when calculating distances.
* `origin_z` (Default: 0) This is the origin z position that is used when calculating distances.
* `use_manhattan_distances` (Default: false). This is a boolean which specifies if the distance rule should use [Manhattan Distance](https://en.wikipedia.org/wiki/Taxicab_geometry) or [Euclidean Distance](https://en.wikipedia.org/wiki/Euclidean_distance).

---

### Spawn Rules

Rules are conditions that are checked for each individual ore block. This allows ore blocks to only spawn on cave walls for example, by defining a rule that only spawns ores if it is touching an air block.

A rule consists of a json object, which contains at least the following entries:

 - `type`: This is the type of the rule. Additional rules may have other required entries depending on the rule.

##### Touching: `"type": "touching"`
This rule indicates that each ore block must touch another block. Touching is defined as the adjacent block on any of the cardinal directions, up, or down. It has one required parameter:
* `block`: This is the block that the vein will be touching.

This rule also has the following optional parameters:
* `min` (Default: 1) This is the minimum number (inclusive) of the block that the ore must touch. Must be an integer.
* `max` (Default: 6) This is the maximum number (inclusive) of the block that the ore must touch. Must be an integer.

---

### Indicators

Indicators are configurable objects that will spawn on the surface when a vein is detected underneath them. An indicator must contain the following entries:

* `blocks` is a [Block Entry](#block-entries). This represents the possible states that the indicator will spawn. This supports weighted entries.

Indicators can also contain the following optional entries

* `rarity` (Default: 10) 1 / N blocks will generate an indicator, provided there is a valid ore block directly underneath.
* `max_depth` (Default: 32) This is the maximum depth for an ore block to generate which would attempt to spawn an indicator.
* `ignore_liquids` (Default: false) If the vein should ignore liquids when trying to spawn indicators. (i.e. should the indicator spawn inside lakes or the ocean?)
* `blocks_under` (Default: accepts all blocks) This is a [Block Entry](#block-entries). The list of blocks that this indicator is allowed to spawn on.
* `replace_surface` (Default: false). This ia a boolean which specifies if the indicator should replace the surface block, rather than spawn on top of it. In this case, `blocks_under` will be the list of blocks to be replaced, at the level of the surface.

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

1. A string, where the value of the string is a block as can be written in a command (such as `/setblock`). This includes:
   - Specifying a block's registry name: `minecraft:iron_ore`
   - A tag, prefixed with `#`: `#minecraft:sand`
   - A block name with properties: `minecraft:oak_stairs[half=top,facing=east]`
2. An JSON object with the field `"block"`, where the value is as above:
```json
{
  "block": "minecraft:dirt"
}
```
3. A JSON array consisting of either of the above forms of a block entry.
   - These can be optionally weighted (e.g. for `ore`). If a weight is not provided, it will default to `1`.
```json
{
  "ore": [
   {
      "block": "minecraft:oak_stairs[half=top]",
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
