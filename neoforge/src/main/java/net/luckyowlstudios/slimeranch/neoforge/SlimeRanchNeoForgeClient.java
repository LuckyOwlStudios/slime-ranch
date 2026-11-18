package net.luckyowlstudios.slimeranch.neoforge;

import net.luckyowlstudios.slimeranch.SlimeRanch;
import net.luckyowlstudios.slimeranch.client.ModSlimeRenderer;
import net.luckyowlstudios.slimeranch.init.ModEntityTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@EventBusSubscriber(modid = SlimeRanch.MOD_ID, value = Dist.CLIENT)
public class SlimeRanchNeoForgeClient {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.BOUNCY_SLIME.get(), ModSlimeRenderer.provider("bouncy_slime"));
        event.registerEntityRenderer(ModEntityTypes.CAVE_SLIME.get(), ModSlimeRenderer.provider("cave_slime"));
        event.registerEntityRenderer(ModEntityTypes.ICE_CUBE.get(), ModSlimeRenderer.provider("ice_cube"));
        event.registerEntityRenderer(ModEntityTypes.PINKY.get(), ModSlimeRenderer.provider("pinky"));
    }
}
