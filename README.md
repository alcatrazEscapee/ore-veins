# Ore Veins

This is a minecraft mod to add realistically shaped veins of ore to your world. Useful for custom maps, modpacks, or just a different survival experience. Everything is fully configurable via json, meaning you can have ore veins of whatever type of shape or size you want.

## Configuration:

Ore Veins will look for all files under config/oreveins/. When you first add ore veins, it will create a default file with some example configuration. Feel free to use or modify this. It is also found here on github at src/main/resources/assets/ore_veins.json.

Each json file in config/oreveins/ should consist of a set of objects, each one being a different type of vein. These represent a single ore type or configuration that will be generated in the world. Each entry must contain the following values:

* `type` is the registry name of the [Vein Type](#Veins) that this entry will spawn. Based on what vein this is, there might be other required or optional values as well.
* `stone` is a [Block entry](#Block-Entries). This represents the blockstates that the ore can spawn in.
* `ore` is a [Block entry](#Block-Entries), with optional weights. This represents the possible states that the vein will spawn.

Each entry can also contain any or all of the following values. If they don't exist, they will assume a default value. These apply to all vein types:

* `count` (Default: 1) Generate at most N veins per chunk. Rarity is applied to each attempt to generate a vein.
* `rarity` (Default: 10) 1 / N chunks will spawn this ore vein.
* `min_y` (Default: 16) Minimum y value for veins to generate at.
* `max_y` (Default: 64) Maximum y value for veins to generate at.
* `density` (Default: 50) Density of the ore vein. Higher values are more dense. (FYI: This number is not a percentage. For 100% density use values >1000)
* `vertical_size` (Default: 15) Vertical radius. This is not an absolute number in blocks, but is close to. Experimentation is required.
* `horizontal_size` (Default: 8) Horizontal radius. This is not an absolute number in blocks, but is close to. Experimentation is required.
* `biomes` (Default: all) Whitelist of biome names or temperatures for a biome to spawn in. Must be a list of strings.
* `biomes_is_whitelist` (Default: true) When false, the biome list becomes a blacklist
* `dimensions` (Default: 0) Whitelist of dimension ids that the ore can spawn in. Must be a list of intergers.
* `dimensions_is_whitelist` (Default: true) When false, the dimension list becomes a blacklist

### Veins

Veins represent different types of shapes or structures that can be spawned. Each entry must define a vein type.

*Spheres*: (`"type": "sphere"`)
This represents a single sphere (or sphereoid, if vertical and horizontal size values are different). This vein type has no additional paramaters.

*Clusters* (`"type": "cluster"`)
This vein represents a scattered group of spheriods.  This vein type has an optional paramater:
* `clusters` (Default: 3) This represents the average number of other clusters that will spawn as part of this vein.

*Vertical Pipe* (`"type": "pipe"`)
This vein represents a single vertical column / cylinder. This vein type has no additional paramaters.

*Cone* (`"type": "cone"`)
This vein represents a vertical cone. The pointy end of the cone can point upwards or downwards. This vein type has two optional paramaters:
* `inverted` (Default: false) If true, the cone will have a pointy end facing down. If false, the pointy end will face up
* `shape` (Default: 0.5) This value determines how pointy the cone will be. It should be between 0.0 and 1.0. Higher values mean less pointy (more cylinderical). Smaller values are more pointy

### Block Entries
A Block Entry can be any of the following:

1. A single string representing a block's registry name: `"ore": "minecraft:iron_ore"`
2. A single object representing a block with metadata: `"ore": { "block": "minecraft:wool", "meta": 3 }`
3. A list of objects (as above). Note that these can be weighted (when used in `ore`) but are not nessecary. If weight is not found for a particular object, it will default to 1.
```
"ore": [
   {
      "block": "minecraft:wool",
      "weight": 4,
      "meta: 3
    },
    {
      "block": "minecraft:coal_ore",
      "weight": 39
    },
    {
      "block": "minecraft:diamond_ore"
    }
  ]
```
