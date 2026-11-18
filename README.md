# Lucky's Slime Ranch

A Minecraft 1.21.1 mod for **Fabric** and **NeoForge** that adds new slimes to the world.

## Slimes

| Slime | Looks | Health | Behaviour | Spawns |
|---|---|---|---|---|
| **Cave Slime** | Gray, no face | 1.5x a vanilla slime of the same size (2 / 6 / 24) | Like a vanilla slime: three sizes, splits when killed, attacks players | Overworld caves: below Y 50, out of sight of the sky, in the dark (not in Mushroom Fields or the Deep Dark) |
| **Bouncy Slime** | Blue, with a face | Same as a vanilla slime | Hops 1.7x as hard as a vanilla slime (about 3.5 blocks high), takes no fall damage, and each hit knocks the target back hard (1.6 + 0.15 per size). Drops Bouncy Slime Balls | Plains, Sunflower Plains and Meadow, in the dark, groups of 1-2 |
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

## Bouncy Slime Ball, Block and Piston

- **Bouncy Slime Ball**: dropped by Bouncy Slimes (small ones, 0-2, more with Looting).
- **Bouncy Slime Block**: 9 balls, and back into 9 balls. Works like a slime block (no fall damage, sneak to stop the
  bounce) but bounces 1.25x as hard, up to a cap of `BouncySlimeBlock.MAX_BOUNCE_SPEED`. It is not sticky for pistons.
- **Bouncy Piston**: a piston plus a Bouncy Slime Ball. It extends instantly when powered, like a vanilla piston would,
  but instead of pushing, every block it would have pushed (up to 12, same rules as a vanilla piston) is launched
  away as a falling block that lands and settles as a block again. Entities in front of the head are launched too.
  Losing power retracts it. Launch speeds are constants at the top of `BouncyPistonBlock`.
