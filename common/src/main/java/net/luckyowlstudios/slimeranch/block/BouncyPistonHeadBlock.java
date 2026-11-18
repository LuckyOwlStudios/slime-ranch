package net.luckyowlstudios.slimeranch.block;

import net.luckyowlstudios.slimeranch.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The head of an extended {@link BouncyPistonBlock}. It is a vanilla piston head in every way and only
 * exists so it can have its own texture, since the vanilla head's texture can't depend on the piston behind it.
 * A mixin makes the base place this block instead of the vanilla head.
 */
public class BouncyPistonHeadBlock extends PistonHeadBlock {

    public BouncyPistonHeadBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(ModBlocks.BOUNCY_PISTON.get());
    }
}
