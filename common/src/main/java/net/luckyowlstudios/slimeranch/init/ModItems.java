package net.luckyowlstudios.slimeranch.init;

import net.luckyowlstudios.slimeranch.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class ModItems {

    public static final Supplier<Item> CAVE_SLIME_SPAWN_EGG = Services.PLATFORM.register(BuiltInRegistries.ITEM, "cave_slime_spawn_egg",
            () -> Services.PLATFORM.createSpawnEgg(ModEntityTypes.CAVE_SLIME, 0x7A7D80, 0x3E4044, new Item.Properties()));

    public static final Supplier<Item> ICE_CUBE_SPAWN_EGG = Services.PLATFORM.register(BuiltInRegistries.ITEM, "ice_cube_spawn_egg",
            () -> Services.PLATFORM.createSpawnEgg(ModEntityTypes.ICE_CUBE, 0xBEE7FA, 0x5FA8D8, new Item.Properties()));

    public static final Supplier<Item> PINKY_SPAWN_EGG = Services.PLATFORM.register(BuiltInRegistries.ITEM, "pinky_spawn_egg",
            () -> Services.PLATFORM.createSpawnEgg(ModEntityTypes.PINKY, 0xF7A8D0, 0xD9558F, new Item.Properties()));

    public static void init() {
    }
}
