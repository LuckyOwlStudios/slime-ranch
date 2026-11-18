package net.luckyowlstudios.slimeranch.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.luckyowlstudios.slimeranch.block.BouncyPistonBlock;
import net.luckyowlstudios.slimeranch.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Makes the Bouncy Piston behave like a vanilla piston where vanilla code names its own blocks.
 */
@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin {

    /** Vanilla pistons refuse to push an extended piston; do the same for the Bouncy Piston. */
    @Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
    private static void slimeranch$extendedBouncyPistonsStay(BlockState state, Level level, BlockPos pos, Direction movementDirection,
                                                             boolean destroyBlocks, Direction pistonFacing, CallbackInfoReturnable<Boolean> cir) {
        if (state.is(ModBlocks.BOUNCY_PISTON.get()) && state.getValue(PistonBaseBlock.EXTENDED)) {
            cir.setReturnValue(false);
        }
    }

    /**
     * {@code moveBlocks} builds the vanilla piston head and looks for it when retracting. A Bouncy Piston
     * uses its own head block, which is the same except for the texture.
     */
    @ModifyExpressionValue(method = "moveBlocks",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/Blocks;PISTON_HEAD:Lnet/minecraft/world/level/block/Block;"),
            require = 0)
    private Block slimeranch$useBouncyHead(Block vanillaHead) {
        return (Object) this instanceof BouncyPistonBlock ? ModBlocks.BOUNCY_PISTON_HEAD.get() : vanillaHead;
    }
}
