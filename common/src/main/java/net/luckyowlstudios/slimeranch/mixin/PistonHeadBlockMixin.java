package net.luckyowlstudios.slimeranch.mixin;

import net.luckyowlstudios.slimeranch.init.ModBlocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * The vanilla piston head only stays in place while the block behind it is a vanilla piston. This lets
 * the Bouncy Piston count too, so it can use the vanilla head and push animation.
 */
@Mixin(PistonHeadBlock.class)
public class PistonHeadBlockMixin {

    @Inject(method = "isFittingBase", at = @At("HEAD"), cancellable = true)
    private void slimeranch$acceptBouncyPiston(BlockState headState, BlockState pistonState, CallbackInfoReturnable<Boolean> cir) {
        if (headState.getValue(PistonHeadBlock.TYPE) == PistonType.DEFAULT
                && pistonState.is(ModBlocks.BOUNCY_PISTON.get())
                && pistonState.getValue(PistonBaseBlock.EXTENDED)
                && pistonState.getValue(PistonBaseBlock.FACING) == headState.getValue(PistonHeadBlock.FACING)) {
            cir.setReturnValue(true);
        }
    }
}
