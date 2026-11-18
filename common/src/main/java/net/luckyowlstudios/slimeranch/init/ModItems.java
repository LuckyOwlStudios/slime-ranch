package net.luckyowlstudios.slimeranch.init;

import net.luckyowlstudios.slimeranch.item.MooshroomMilkItem;
import net.luckyowlstudios.slimeranch.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.function.Supplier;

public class ModItems {

    public static final Supplier<Item> CAVE_SLIME_SPAWN_EGG = Services.PLATFORM.register(BuiltInRegistries.ITEM, "cave_slime_spawn_egg",
            () -> Services.PLATFORM.createSpawnEgg(ModEntityTypes.CAVE_SLIME, 0x7A7D80, 0x3E4044, new Item.Properties()));

    public static final Supplier<Item> BOUNCY_SLIME_SPAWN_EGG = Services.PLATFORM.register(BuiltInRegistries.ITEM, "bouncy_slime_spawn_egg",
            () -> Services.PLATFORM.createSpawnEgg(ModEntityTypes.BOUNCY_SLIME, 0x4C8BFF, 0x1F46B8, new Item.Properties()));

    public static final Supplier<Item> BOUNCY_SLIME_BALL = Services.PLATFORM.register(BuiltInRegistries.ITEM, "bouncy_slime_ball",
            () -> new Item(new Item.Properties()));

    public static final Supplier<Item> BOUNCY_SLIME_BLOCK = Services.PLATFORM.register(BuiltInRegistries.ITEM, "bouncy_slime_block",
            () -> new BlockItem(ModBlocks.BOUNCY_SLIME_BLOCK.get(), new Item.Properties()));

    public static final Supplier<Item> BOUNCY_PISTON = Services.PLATFORM.register(BuiltInRegistries.ITEM, "bouncy_piston",
            () -> new BlockItem(ModBlocks.BOUNCY_PISTON.get(), new Item.Properties()));

    public static final Supplier<Item> ICE_CUBE_SPAWN_EGG = Services.PLATFORM.register(BuiltInRegistries.ITEM, "ice_cube_spawn_egg",
            () -> Services.PLATFORM.createSpawnEgg(ModEntityTypes.ICE_CUBE, 0xBEE7FA, 0x5FA8D8, new Item.Properties()));

    public static final Supplier<Item> PINKY_SPAWN_EGG = Services.PLATFORM.register(BuiltInRegistries.ITEM, "pinky_spawn_egg",
            () -> Services.PLATFORM.createSpawnEgg(ModEntityTypes.PINKY, 0xF7A8D0, 0xD9558F, new Item.Properties()));

    public static final Supplier<Item> MOOSHROOM_MILK = Services.PLATFORM.register(BuiltInRegistries.ITEM, "mooshroom_milk",
            () -> new MooshroomMilkItem(new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static void init() {
    }
}
