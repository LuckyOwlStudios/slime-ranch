# Lucky's Slime Ranch

A Minecraft 1.21.1 mod for **Fabric** and **NeoForge** that adds new slimes to the world.

## Slimes

| Slime | Looks | Health | Behaviour | Spawns |
|---|---|---|---|---|
| **Cave Slime** | Gray, no face | 1.5x a vanilla slime of the same size (2 / 6 / 24) | Like a vanilla slime: three sizes, splits when killed, attacks players | Overworld caves: below Y 50, out of sight of the sky, in the dark (not in Mushroom Fields or the Deep Dark) |
| **Ice Cube** | Icy light blue, with a face | Same as a vanilla slime | Like a vanilla slime, but each hit adds powder-snow freeze to the target: about two hits freeze it solid (slowed, freeze damage) until it thaws. Leather armor protects, and the Ice Cube itself can't freeze | Snowy biomes (`#c:is_snowy`), in the dark, groups of 1-2 |
| **Pinky** | Small, pink, with a face | 30 (15 hearts) | Always small, hops every 2-6 ticks (vanilla waits 10-29) and moves faster; harmless, doesn't chase players | Cherry Groves, on grass in daylight, in groups of 2-4 |

All of them drop slime balls (the Ice Cube also drops snowballs) (Pinky drops 1-3, more with Looting) and have spawn eggs in the Spawn Eggs tab.

Tuning values are constants at the top of `CaveSlime` and `Pinky` in `common/.../entity`. Spawn weights live in
`SlimeRanchFabric` and in the NeoForge biome modifiers under `neoforge/src/main/resources/data/slimeranch/neoforge/biome_modifier`;
keep the two in sync.

## Layout

MultiLoader layout: shared code in `common`, loader entry points in `fabric` and `neoforge`. Loader-specific calls go
through `platform/services/IPlatformHelper`.

## Building

```
./gradlew build
```

Jars end up in `fabric/build/libs` and `neoforge/build/libs`. Run the game with `./gradlew :fabric:runClient` or
`./gradlew :neoforge:runClient`.

## Mooshroom Milk (slime farming)

Use an empty bucket on an adult mooshroom to get **Mooshroom Milk** (cows still give regular milk, and bowls still
give stew). Use the milk on any slime, including magma cubes, Cave Slimes and Ice Cubes, to grow it to the next size
(1 -> 2 -> 4, capped at `MooshroomMilkItem.MAX_SIZE`). The bucket is returned, and a fed slime no longer despawns.
Bigger slimes split into more small slimes, so more slime balls. Pinky always stays small and can't be grown.
