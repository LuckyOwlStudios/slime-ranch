package net.luckyowlstudios.slimeranch.mixin;

import net.luckyowlstudios.slimeranch.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Vanilla pistons refuse to push an extended piston. This does the same for the Bouncy Piston; a
 * retracted one can be pushed like any other block.
 */
@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin {

    @Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
    private static void slimeranch$extendedBouncyPistonsStay(BlockState state, Level level, BlockPos pos, Direction movementDirection,
                                                             boolean destroyBlocks, Direction pistonFacing, CallbackInfoReturnable<Boolean> cir) {
        if (state.is(ModBlocks.BOUNCY_PISTON.get()) && state.getValue(PistonBaseBlock.EXTENDED)) {
            cir.setReturnValue(false);
        }
    }
}
