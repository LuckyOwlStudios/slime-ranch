package net.luckyowlstudios.slimeranch.client;

import net.luckyowlstudios.slimeranch.SlimeRanch;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Slime;

/**
 * Draws a slime with the vanilla slime model (inner body, face and outer gel layer) and this mod's
 * own texture, which uses the same layout as the vanilla slime texture.
 */
public class ModSlimeRenderer extends SlimeRenderer {

    private final ResourceLocation texture;

    public ModSlimeRenderer(EntityRendererProvider.Context context, String name) {
        super(context);
        this.texture = SlimeRanch.id("textures/entity/slime/" + name + ".png");
    }

    public static EntityRendererProvider<Slime> provider(String name) {
        return context -> new ModSlimeRenderer(context, name);
    }

    @Override
    public ResourceLocation getTextureLocation(Slime entity) {
        return this.texture;
    }
}
