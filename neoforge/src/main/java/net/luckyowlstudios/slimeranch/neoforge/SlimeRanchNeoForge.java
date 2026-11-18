package net.luckyowlstudios.slimeranch.neoforge;

import net.luckyowlstudios.slimeranch.SlimeRanch;
import net.luckyowlstudios.slimeranch.entity.CaveSlime;
import net.luckyowlstudios.slimeranch.entity.IceCube;
import net.luckyowlstudios.slimeranch.entity.Pinky;
import net.luckyowlstudios.slimeranch.init.ModEntityTypes;
import net.luckyowlstudios.slimeranch.init.ModItems;
import net.luckyowlstudios.slimeranch.neoforge.platform.NeoForgePlatformHelper;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(SlimeRanch.MOD_ID)
public class SlimeRanchNeoForge {

    public SlimeRanchNeoForge(IEventBus modEventBus) {
        SlimeRanch.init();
        NeoForgePlatformHelper.registerAll(modEventBus);

        modEventBus.addListener(SlimeRanchNeoForge::registerAttributes);
        modEventBus.addListener(SlimeRanchNeoForge::registerSpawnPlacements);
        modEventBus.addListener(SlimeRanchNeoForge::addCreative);
    }

    private static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.CAVE_SLIME.get(), Monster.createMonsterAttributes().build());
        event.put(ModEntityTypes.ICE_CUBE.get(), Monster.createMonsterAttributes().build());
        event.put(ModEntityTypes.PINKY.get(), Monster.createMonsterAttributes().build());
    }

    private static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        // Spawn biomes come from the biome modifiers in data/slimeranch/neoforge/biome_modifier
        event.register(ModEntityTypes.CAVE_SLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                CaveSlime::checkCaveSlimeSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntityTypes.ICE_CUBE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                IceCube::checkIceCubeSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntityTypes.PINKY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Pinky::checkPinkySpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    private static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModItems.CAVE_SLIME_SPAWN_EGG.get());
            event.accept(ModItems.ICE_CUBE_SPAWN_EGG.get());
            event.accept(ModItems.PINKY_SPAWN_EGG.get());
        }
    }
}
