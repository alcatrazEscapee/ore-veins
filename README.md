# Ore Veins

This is a minecraft mod to add realistically shaped veins of ore to your world. Useful for custom maps, modpacks, or just a different survival experience. Everything is fully configurable via json, meaning you can have ore veins of whatever type of shape or size you want.

## Configuration:

Ore Veins will look for all files under config/oreveins/. When you first add ore veins, it will create a default file with some example configuration. Feel free to use or modify this. It is also found here on github at src/main/resources/assets/ore_veins.json.

Each json file in config/oreveins/ should consist of a number of *Ore* objects. These represent a single ore type or configuration that will be generated in the world. Each *Ore Entry* must contain the following two values:

* `type` is the registry name of the *Vein Type* that this entry will spawn. Based on what *Vein* this is, there might be other required values as well.
* `stone` is a Block entry (see below) This represents the blockstates that the ore can spawn in.

Each *Ore Entry* can also contain any or all of the following values. If they don't exist, they will assume a default value. These apply to all vein types:

* `count` (Default: 1) Generate at most N veins per chunk. Rarity is applied to each attempt to generate a vein.
* `rarity` (Default: 10) 1 / N chunks will spawn this ore vein.
* `min_y` (Default: 16) Minimum y value for veins to generate at.
* `max_y` (Default: 64) Maximum y value for veins to generate at.
* `vertical_size` (Default: 15) Vertical size modifier. This is not an absolute number in blocks, but is close to. Experimentation is required.
* `horizontal_size` (Default: 8) Horizontal size modifier. This is not an absolute number in blocks, but is close to. Experimentation is required.
* `biomes` (Default: all) Whitelist of biome names or temperatures for a biome to spawn in. Must be a list of strings.
* `biomes_is_whitelist` (Default: true) When false, the biome list becomes a blacklist
* `dimensions` (Default: 0) Whitelist of dimension ids that the ore can spawn in. Must be a list of intergers.
* `dimensions_is_whitelist` (Default: true) When false, the dimension list becomes a blacklist

### Veins

Veins represent different types of structures that can be spawned. Currently there are two: Clusters, and Spheres. All vein types are below.

*Spheres*: (Registry name: `oreveins:sphere`)
This represents a single sphere. When using this vein type, you must also provide the following values:
* `ore` is a *Block Entry* which represents a weighted list of all possible blockstates for the ore to spawn
The following values are optional when using this vein type:
* `density` (Default: 50) This represents the rough density of the ore vein. Higher values are more dense. Expirementation is required.

*Clusters* (Registry name: `oreveins:cluster`)
This vein represents a scattered group of spheriods.  When using this vein type, you must also provide the following values:
* `ore` is a *Block Entry* which represents a weighted list of all possible blockstates for the ore to spawn
The following values are optional when using this vein type:
* `density` (Default: 50) This represents the rough density of the ore vein. Higher values are more dense. Expirementation is required.
* `clusters` (Default: 3) This represents the average number of other clusters that will spawn as part of this vein.

### Block Entries
A Block Entry can be any of the following:

1. A single string representing a block's registry name: `"ore": "minecraft:iron_ore"`
2. A single object representing a block with metadata: `"ore": { "block": "minecraft:wool", "meta": 3 }`
3. A list of objects (as above) that can be weighted in terms of how common they are:
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
      "block": "minecraft:diamond_ore",
      "weight": 1
    }
  ]
```
