package net.luckyowlstudios.slimeranch.init;

import net.luckyowlstudios.slimeranch.entity.BouncySlime;
import net.luckyowlstudios.slimeranch.entity.CaveSlime;
import net.luckyowlstudios.slimeranch.entity.HungrySlime;
import net.luckyowlstudios.slimeranch.entity.IceCube;
import net.luckyowlstudios.slimeranch.entity.Pinky;
import net.luckyowlstudios.slimeranch.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

public class ModEntityTypes {

    // Same base dimensions as a vanilla slime; the slime's size multiplier scales them
    public static final Supplier<EntityType<CaveSlime>> CAVE_SLIME = Services.PLATFORM.register(BuiltInRegistries.ENTITY_TYPE, "cave_slime",
            () -> EntityType.Builder.of(CaveSlime::new, MobCategory.MONSTER)
                    .sized(0.52F, 0.52F)
                    .eyeHeight(0.325F)
                    .spawnDimensionsScale(4.0F)
                    .clientTrackingRange(10)
                    .build("cave_slime"));

    public static final Supplier<EntityType<IceCube>> ICE_CUBE = Services.PLATFORM.register(BuiltInRegistries.ENTITY_TYPE, "ice_cube",
            () -> EntityType.Builder.of(IceCube::new, MobCategory.MONSTER)
                    .sized(0.52F, 0.52F)
                    .eyeHeight(0.325F)
                    .spawnDimensionsScale(4.0F)
                    .clientTrackingRange(10)
                    .build("ice_cube"));

    public static final Supplier<EntityType<BouncySlime>> BOUNCY_SLIME = Services.PLATFORM.register(BuiltInRegistries.ENTITY_TYPE, "bouncy_slime",
            () -> EntityType.Builder.of(BouncySlime::new, MobCategory.MONSTER)
                    .sized(0.52F, 0.52F)
                    .eyeHeight(0.325F)
                    .spawnDimensionsScale(4.0F)
                    .clientTrackingRange(10)
                    .build("bouncy_slime"));

    public static final Supplier<EntityType<HungrySlime>> HUNGRY_SLIME = Services.PLATFORM.register(BuiltInRegistries.ENTITY_TYPE, "hungry_slime",
            () -> EntityType.Builder.of(HungrySlime::new, MobCategory.MONSTER)
                    .sized(0.52F, 0.52F)
                    .eyeHeight(0.325F)
                    .spawnDimensionsScale(4.0F)
                    .clientTrackingRange(10)
                    .build("hungry_slime"));

    // Pinky is always the smallest slime size, so it never needs extra spawn space
    public static final Supplier<EntityType<Pinky>> PINKY = Services.PLATFORM.register(BuiltInRegistries.ENTITY_TYPE, "pinky",
            () -> EntityType.Builder.of(Pinky::new, MobCategory.CREATURE)
                    .sized(0.52F, 0.52F)
                    .eyeHeight(0.325F)
                    .clientTrackingRange(10)
                    .build("pinky"));

    public static void init() {
    }
}
