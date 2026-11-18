package net.luckyowlstudios.slimeranch.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.luckyowlstudios.slimeranch.SlimeRanch;
import net.luckyowlstudios.slimeranch.entity.CaveSlime;
import net.luckyowlstudios.slimeranch.event.MilkingEvents;
import net.luckyowlstudios.slimeranch.entity.IceCube;
import net.luckyowlstudios.slimeranch.entity.Pinky;
import net.luckyowlstudios.slimeranch.init.ModEntityTypes;
import net.luckyowlstudios.slimeranch.init.ModItems;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;

public class SlimeRanchFabric implements ModInitializer {

    // Keep these in sync with the NeoForge biome modifiers in data/slimeranch/neoforge/biome_modifier
    private static final int CAVE_SLIME_WEIGHT = 30, CAVE_SLIME_MIN_GROUP = 1, CAVE_SLIME_MAX_GROUP = 2;
    private static final int ICE_CUBE_WEIGHT = 40, ICE_CUBE_MIN_GROUP = 1, ICE_CUBE_MAX_GROUP = 2;
    private static final int PINKY_WEIGHT = 8, PINKY_MIN_GROUP = 2, PINKY_MAX_GROUP = 4;

    @Override
    public void onInitialize() {
        SlimeRanch.init();
        registerEntities();
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(entries -> {
            entries.accept(ModItems.CAVE_SLIME_SPAWN_EGG.get());
            entries.accept(ModItems.ICE_CUBE_SPAWN_EGG.get());
            entries.accept(ModItems.PINKY_SPAWN_EGG.get());
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries ->
                entries.addAfter(Items.MILK_BUCKET, ModItems.MOOSHROOM_MILK.get()));
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) ->
                MilkingEvents.onInteractEntity(player, hand, entity));
    }

    private static void registerEntities() {
        FabricDefaultAttributeRegistry.register(ModEntityTypes.CAVE_SLIME.get(), Monster.createMonsterAttributes());
        FabricDefaultAttributeRegistry.register(ModEntityTypes.ICE_CUBE.get(), Monster.createMonsterAttributes());
        FabricDefaultAttributeRegistry.register(ModEntityTypes.PINKY.get(), Monster.createMonsterAttributes());

        SpawnPlacements.register(ModEntityTypes.CAVE_SLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                CaveSlime::checkCaveSlimeSpawnRules);
        SpawnPlacements.register(ModEntityTypes.ICE_CUBE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                IceCube::checkIceCubeSpawnRules);
        SpawnPlacements.register(ModEntityTypes.PINKY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Pinky::checkPinkySpawnRules);

        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(), MobCategory.MONSTER, ModEntityTypes.CAVE_SLIME.get(),
                CAVE_SLIME_WEIGHT, CAVE_SLIME_MIN_GROUP, CAVE_SLIME_MAX_GROUP);
        BiomeModifications.addSpawn(BiomeSelectors.tag(ConventionalBiomeTags.IS_SNOWY), MobCategory.MONSTER, ModEntityTypes.ICE_CUBE.get(),
                ICE_CUBE_WEIGHT, ICE_CUBE_MIN_GROUP, ICE_CUBE_MAX_GROUP);
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(Biomes.CHERRY_GROVE), MobCategory.CREATURE, ModEntityTypes.PINKY.get(),
                PINKY_WEIGHT, PINKY_MIN_GROUP, PINKY_MAX_GROUP);
    }
}
