package net.luckyowlstudios.slimeranch.neoforge.platform;

import net.luckyowlstudios.slimeranch.SlimeRanch;
import net.luckyowlstudios.slimeranch.platform.services.IPlatformHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class NeoForgePlatformHelper implements IPlatformHelper {

    private static final Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>> REGISTERS = new LinkedHashMap<>();

    /** Hooks every deferred register created through {@link #register} up to the mod event bus. */
    public static void registerAll(IEventBus modEventBus) {
        REGISTERS.values().forEach(register -> register.register(modEventBus));
    }

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R, T extends R> Supplier<T> register(Registry<R> registry, String name, Supplier<T> factory) {
        DeferredRegister<R> register = (DeferredRegister<R>) REGISTERS.computeIfAbsent(registry.key(),
                key -> DeferredRegister.create(registry.key(), SlimeRanch.MOD_ID));
        return register.register(name, factory);
    }

    @Override
    public Item createSpawnEgg(Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor, Item.Properties properties) {
        return new DeferredSpawnEggItem(type, backgroundColor, highlightColor, properties);
    }
}
