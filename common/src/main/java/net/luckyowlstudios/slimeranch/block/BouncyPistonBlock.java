package net.luckyowlstudios.slimeranch.block;

import com.mojang.serialization.MapCodec;
import net.luckyowlstudios.slimeranch.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;

import java.util.List;

/**
 * A piston that doesn't push blocks, it launches them. When it gets power its head shoots out at once
 * and every block it would have pushed (up to the usual 12) is thrown away from it as a falling block,
 * which lands and settles as a block again. Entities standing where the head appears are thrown too.
 * Losing power pulls the head back in.
 */
public class BouncyPistonBlock extends DirectionalBlock {

    public static final MapCodec<BouncyPistonBlock> CODEC = simpleCodec(BouncyPistonBlock::new);
    public static final BooleanProperty EXTENDED = BlockStateProperties.EXTENDED;

    /** Launch speed in blocks per tick; falling blocks only lose speed to air drag and gravity. */
    public static final double LAUNCH_SPEED = 0.9;
    /** Extra upward speed when launching sideways, so blocks fly in an arc instead of skidding. */
    public static final double LAUNCH_LIFT = 0.25;
    /** Entities are launched a little harder than blocks, since they have more drag and gravity. */
    public static final double ENTITY_LAUNCH_SPEED = 1.3;
    public static final double ENTITY_LAUNCH_LIFT = 0.45;

    private static final VoxelShape EXTENDED_UP = Block.box(0, 0, 0, 16, 12, 16);
    private static final VoxelShape EXTENDED_DOWN = Block.box(0, 4, 0, 16, 16, 16);
    private static final VoxelShape EXTENDED_NORTH = Block.box(0, 0, 4, 16, 16, 16);
    private static final VoxelShape EXTENDED_SOUTH = Block.box(0, 0, 0, 16, 16, 12);
    private static final VoxelShape EXTENDED_EAST = Block.box(0, 0, 0, 12, 16, 16);
    private static final VoxelShape EXTENDED_WEST = Block.box(4, 0, 0, 16, 16, 16);

    public BouncyPistonBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(EXTENDED, false));
    }

    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, EXTENDED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getNearestLookingDirection().getOpposite())
                .setValue(EXTENDED, false);
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
        if (!state.getValue(EXTENDED)) return Shapes.block();
        return switch (state.getValue(FACING)) {
            case DOWN -> EXTENDED_DOWN;
            case UP -> EXTENDED_UP;
            case NORTH -> EXTENDED_NORTH;
            case SOUTH -> EXTENDED_SOUTH;
            case WEST -> EXTENDED_WEST;
            case EAST -> EXTENDED_EAST;
        };
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!oldState.is(state.getBlock())) {
            this.scheduleUpdate(level, pos);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        this.scheduleUpdate(level, pos);
    }

    private void scheduleUpdate(Level level, BlockPos pos) {
        if (!level.isClientSide() && !level.getBlockTicks().hasScheduledTick(pos, this)) {
            level.scheduleTick(pos, this, 1);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        Direction facing = state.getValue(FACING);
        boolean powered = hasNeighborSignal(level, pos, facing);
        boolean extended = state.getValue(EXTENDED);
        if (powered && !extended) {
            this.extend(level, pos, state, facing);
        } else if (!powered && extended) {
            this.retract(level, pos, state, facing);
        }
    }

    /** The same power check a vanilla piston uses, including quasi-connectivity. */
    private static boolean hasNeighborSignal(Level level, BlockPos pos, Direction facing) {
        for (Direction direction : Direction.values()) {
            if (direction != facing && level.hasSignal(pos.relative(direction), direction)) return true;
        }
        if (level.hasSignal(pos, Direction.DOWN)) return true;
        BlockPos above = pos.above();
        for (Direction direction : Direction.values()) {
            if (direction != Direction.DOWN && level.hasSignal(above.relative(direction), direction)) return true;
        }
        return false;
    }

    private void extend(ServerLevel level, BlockPos pos, BlockState state, Direction facing) {
        BlockPos headPos = pos.relative(facing);
        PistonStructureResolver resolver = new PistonStructureResolver(level, pos, facing, true);
        // Too many blocks in the way, or something immovable: stay retracted, like a vanilla piston
        if (!resolver.resolve()) return;

        for (BlockPos destroyed : resolver.getToDestroy()) {
            level.destroyBlock(destroyed, true);
        }
        // Farthest first, so a block never sits in the way of the one behind it
        List<BlockPos> toLaunch = resolver.getToPush();
        for (int i = toLaunch.size() - 1; i >= 0; i--) {
            launchBlock(level, toLaunch.get(i), facing);
        }

        BlockState head = ModBlocks.BOUNCY_PISTON_HEAD.get().defaultBlockState().setValue(FACING, facing);
        level.setBlock(headPos, head, 3);
        level.setBlock(pos, state.setValue(EXTENDED, true), 3);

        for (Entity entity : level.getEntities((Entity) null, new AABB(headPos), BouncyPistonBlock::canLaunch)) {
            launchEntity(entity, facing);
        }

        level.playSound(null, pos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.6F, level.random.nextFloat() * 0.25F + 0.5F);
        level.gameEvent(GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Context.of(state));
    }

    private void retract(ServerLevel level, BlockPos pos, BlockState state, Direction facing) {
        // Reset the base first: removing the head with an extended base still behind it would break the base
        level.setBlock(pos, state.setValue(EXTENDED, false), 3);
        BlockPos headPos = pos.relative(facing);
        if (level.getBlockState(headPos).is(ModBlocks.BOUNCY_PISTON_HEAD.get())) {
            level.removeBlock(headPos, false);
        }
        level.playSound(null, pos, SoundEvents.PISTON_CONTRACT, SoundSource.BLOCKS, 0.6F, level.random.nextFloat() * 0.15F + 0.6F);
        level.gameEvent(GameEvent.BLOCK_DEACTIVATE, pos, GameEvent.Context.of(state));
    }

    private static void launchBlock(ServerLevel level, BlockPos pos, Direction facing) {
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) return;
        FallingBlockEntity block = FallingBlockEntity.fall(level, pos, state);
        double lift = facing.getAxis().isHorizontal() ? LAUNCH_LIFT : 0.0;
        block.setDeltaMovement(facing.getStepX() * LAUNCH_SPEED, facing.getStepY() * LAUNCH_SPEED + lift, facing.getStepZ() * LAUNCH_SPEED);
        block.hurtMarked = true;
    }

    private static boolean canLaunch(Entity entity) {
        return entity.getPistonPushReaction() != PushReaction.IGNORE && !(entity instanceof FallingBlockEntity);
    }

    private static void launchEntity(Entity entity, Direction facing) {
        double lift = facing.getAxis().isHorizontal() ? ENTITY_LAUNCH_LIFT : 0.0;
        entity.setDeltaMovement(facing.getStepX() * ENTITY_LAUNCH_SPEED, facing.getStepY() * ENTITY_LAUNCH_SPEED + lift, facing.getStepZ() * ENTITY_LAUNCH_SPEED);
        entity.hurtMarked = true;
    }
}
