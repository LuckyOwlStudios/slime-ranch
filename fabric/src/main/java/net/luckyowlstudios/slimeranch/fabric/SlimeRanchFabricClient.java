package net.luckyowlstudios.slimeranch.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.luckyowlstudios.slimeranch.client.ModSlimeRenderer;
import net.luckyowlstudios.slimeranch.init.ModBlocks;
import net.luckyowlstudios.slimeranch.init.ModEntityTypes;
import net.minecraft.client.renderer.RenderType;

public class SlimeRanchFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BOUNCY_SLIME_BLOCK.get(), RenderType.translucent());
        EntityRendererRegistry.register(ModEntityTypes.BOUNCY_SLIME.get(), ModSlimeRenderer.provider("bouncy_slime"));
        EntityRendererRegistry.register(ModEntityTypes.HUNGRY_SLIME.get(), ModSlimeRenderer.provider("hungry_slime"));
        EntityRendererRegistry.register(ModEntityTypes.CAVE_SLIME.get(), ModSlimeRenderer.provider("cave_slime"));
        EntityRendererRegistry.register(ModEntityTypes.ICE_CUBE.get(), ModSlimeRenderer.provider("ice_cube"));
        EntityRendererRegistry.register(ModEntityTypes.PINKY.get(), ModSlimeRenderer.provider("pinky"));
    }
}
