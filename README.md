# Ore Veins

This is a minecraft mod to add realistically shaped veins of ore to your world. Useful for custom maps, modpacks, or just a different survival experience. Everything is fully configurable via json, meaning you can have ore veins of whatever type of shape or size you want.

### Options:

For examples look at the default json, found in src/main/resources/assets/ore_veins.json.

Each entry must consist of at least two objects:

* `ore` is a Block entry (see below) This represents the blockstate for the ore to be spawned.
* `stone` is a Block entry (see below) This represents the blockstates that the ore can spawn in.

Each ore entry can also have the following properties. If they are not present they will assume their default value.

* `rarity` (Default: 10) 1 / N chunks will spawn this ore vein.
* `density` (Default: 50) Controls the density of ore veins. Higher values are more dense. Experimentation required for best results.
* `min_y` (Default: 16) Minimum y value for veins to generate at.
* `max_y` (Default: 64) Maximum y value for veins to generate at.
* `vertical_size` (Default: 15) Vertical size modifier. This is not an absolute number in blocks, but is close to. Experimentation is required.
* `horizontal_size` (Default: 8) Horizontal size modifier. This is not an absolute number in blocks, but is close to. Experimentation is required.
* `biomes` (Default: all) Whitelist of biome names or temperatures for a biome to spawn in. Must be a list of strings.
* `biomes_is_whitelist` (Default: true) When false, the biome list becomes a blacklist
* `dimensions` (Default: 0) Whitelist of dimension ids that the ore can spawn in. Must be a list of intergers.
* `dimensions_is_whitelist` (Default: true) When false, the dimension list becomes a blacklist

A Block entry can be:

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
