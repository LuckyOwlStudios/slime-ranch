package net.luckyowlstudios.slimeranch;

import com.mojang.logging.LogUtils;
import net.luckyowlstudios.slimeranch.init.ModEntityTypes;
import net.luckyowlstudios.slimeranch.init.ModItems;
import net.luckyowlstudios.slimeranch.platform.Services;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

/**
 * Shared entry point. Each loader module calls {@link #init()} from its own entry point.
 */
public class SlimeRanch {
    public static final String MOD_ID = "slimeranch";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        LOGGER.info("Loading Slime Ranch on {}", Services.PLATFORM.getPlatformName());
        // Order matters on Fabric, where registration happens immediately: entity types before the
        // spawn eggs that reference them.
        ModEntityTypes.init();
        ModItems.init();
    }

    public static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }
}
