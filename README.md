# Lucky's Slime Ranch

A Minecraft 1.21.1 mod for **Fabric** and **NeoForge** that adds new slimes to the world.

## Slimes

| Slime | Looks | Health | Behaviour | Spawns |
|---|---|---|---|---|
| **Cave Slime** | Gray, no face | 1.5x a vanilla slime of the same size (2 / 6 / 24) | Like a vanilla slime: three sizes, splits when killed, attacks players | Overworld caves: below Y 50, out of sight of the sky, in the dark (not in Mushroom Fields or the Deep Dark) |
| **Pinky** | Small, pink, with a face | 30 (15 hearts) | Always small, hops every 2-6 ticks (vanilla waits 10-29) and moves faster; harmless, doesn't chase players | Cherry Groves, on grass in daylight, in groups of 2-4 |

Both drop slime balls (Pinky drops 1-3, more with Looting) and have spawn eggs in the Spawn Eggs tab.

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
