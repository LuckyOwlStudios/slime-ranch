package net.luckyowlstudios.slimeranch.block;

import com.mojang.serialization.MapCodec;
import net.luckyowlstudios.slimeranch.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The head of an extended {@link BouncyPistonBlock}. It only exists while the base behind it is
 * extended, and breaking it breaks the base too, like a vanilla piston head.
 */
public class BouncyPistonHeadBlock extends DirectionalBlock {

    public static final MapCodec<BouncyPistonHeadBlock> CODEC = simpleCodec(BouncyPistonHeadBlock::new);

    // A 4 pixel thick plate at the front and a 4x4 pixel arm back to the base
    private static final VoxelShape UP = Shapes.or(Block.box(0, 12, 0, 16, 16, 16), Block.box(6, 0, 6, 10, 12, 10));
    private static final VoxelShape DOWN = Shapes.or(Block.box(0, 0, 0, 16, 4, 16), Block.box(6, 4, 6, 10, 16, 10));
    private static final VoxelShape NORTH = Shapes.or(Block.box(0, 0, 0, 16, 16, 4), Block.box(6, 6, 4, 10, 10, 16));
    private static final VoxelShape SOUTH = Shapes.or(Block.box(0, 0, 12, 16, 16, 16), Block.box(6, 6, 0, 10, 10, 12));
    private static final VoxelShape EAST = Shapes.or(Block.box(12, 0, 0, 16, 16, 16), Block.box(0, 6, 6, 12, 10, 10));
    private static final VoxelShape WEST = Shapes.or(Block.box(0, 0, 0, 4, 16, 16), Block.box(4, 6, 6, 16, 10, 10));

    public BouncyPistonHeadBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case DOWN -> DOWN;
            case UP -> UP;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }

    private static boolean isBase(BlockState base, Direction facing) {
        return base.is(ModBlocks.BOUNCY_PISTON.get())
                && base.getValue(BouncyPistonBlock.EXTENDED)
                && base.getValue(FACING) == facing;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        return isBase(level.getBlockState(pos.relative(facing.getOpposite())), facing);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == state.getValue(FACING).getOpposite() && !state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && player.getAbilities().instabuild) {
            // Creative players remove the whole piston without it dropping
            Direction facing = state.getValue(FACING);
            BlockPos basePos = pos.relative(facing.getOpposite());
            if (isBase(level.getBlockState(basePos), facing)) {
                level.removeBlock(basePos, false);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            super.onRemove(state, level, pos, newState, movedByPiston);
            Direction facing = state.getValue(FACING);
            BlockPos basePos = pos.relative(facing.getOpposite());
            if (isBase(level.getBlockState(basePos), facing)) {
                level.destroyBlock(basePos, true);
            }
        }
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(ModBlocks.BOUNCY_PISTON.get());
    }
}
