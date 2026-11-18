package net.luckyowlstudios.slimeranch.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.luckyowlstudios.slimeranch.init.ModBlocks;
import net.minecraft.client.renderer.blockentity.PistonHeadRenderer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * The renderer for a moving piston part only gives a vanilla piston head its short or long arm while
 * it extends. Treat the Bouncy Piston head the same way.
 */
@Mixin(PistonHeadRenderer.class)
public class PistonHeadRendererMixin {

    @WrapOperation(method = "render(Lnet/minecraft/world/level/block/piston/PistonMovingBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"),
            require = 0)
    private boolean slimeranch$bouncyHeadIsAHead(BlockState state, Block block, Operation<Boolean> original) {
        return original.call(state, block) || (block == Blocks.PISTON_HEAD && state.is(ModBlocks.BOUNCY_PISTON_HEAD.get()));
    }
}
