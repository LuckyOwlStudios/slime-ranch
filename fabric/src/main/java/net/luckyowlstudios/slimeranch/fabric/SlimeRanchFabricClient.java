package net.luckyowlstudios.slimeranch.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.luckyowlstudios.slimeranch.client.ModSlimeRenderer;
import net.luckyowlstudios.slimeranch.init.ModEntityTypes;

public class SlimeRanchFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntityTypes.CAVE_SLIME.get(), ModSlimeRenderer.provider("cave_slime"));
        EntityRendererRegistry.register(ModEntityTypes.ICE_CUBE.get(), ModSlimeRenderer.provider("ice_cube"));
        EntityRendererRegistry.register(ModEntityTypes.PINKY.get(), ModSlimeRenderer.provider("pinky"));
    }
}
