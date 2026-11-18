package net.luckyowlstudios.slimeranch.platform.services;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

/**
 * Everything the common code needs from the mod loader.
 */
public interface IPlatformHelper {

    /** The name of the loader this build is running on, e.g. "NeoForge" or "Fabric". */
    String getPlatformName();

    /**
     * Registers an object under this mod's namespace. On NeoForge registration is deferred until the
     * registry event fires, so only call {@link Supplier#get()} on the result after registration.
     */
    <R, T extends R> Supplier<T> register(Registry<R> registry, String name, Supplier<T> factory);

    /** A spawn egg; NeoForge needs its deferred spawn egg because entity types register after items there. */
    Item createSpawnEgg(Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor, Item.Properties properties);
}
