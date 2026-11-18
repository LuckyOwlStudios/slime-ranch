package net.luckyowlstudios.slimeranch.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * A vanilla piston (same model, head, push animation and rules) that launches the blocks it would push
 * instead of pushing them. Players and mobs standing where the head appears are launched too. Just before the head starts to extend, every block in front of it that it
 * could have pushed (up to 12) is thrown away as a falling block, which lands and settles as a block again.
 */
public class BouncyPistonBlock extends PistonBaseBlock {

    /** Launch speed in blocks per tick; falling blocks only lose speed to air drag and gravity. */
    public static final double LAUNCH_SPEED = 0.9;
    /** Extra upward speed when launching sideways, so blocks fly in an arc instead of skidding. */
    public static final double LAUNCH_LIFT = 0.25;

    /** Entities are launched a little harder than blocks, since they have more drag and gravity. */
    public static final double ENTITY_LAUNCH_SPEED = 1.3;
    public static final double ENTITY_LAUNCH_LIFT = 0.45;

    public BouncyPistonBlock(Properties properties) {
        super(false, properties);
    }

    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        // Event 0 is the head extending
        if (id == 0) {
            Direction facing = state.getValue(FACING);
            if (level instanceof ServerLevel serverLevel) {
                // Vanilla ignores the event when power was lost in the meantime, so do the same
                if (hasNeighborSignal(level, pos, facing)) {
                    launchBlocks(serverLevel, pos, facing);
                }
            } else {
                // The event reaches the client before the server's block changes do, so the client still
                // sees the blocks and would run its own push animation on them. That leaves a block that
                // only exists on the client one space further on. Clear them here, like the server did.
                clearBlocksOnClient(level, pos, facing);
            }
        }
        return super.triggerEvent(state, level, pos, id, param);
    }

    private static void clearBlocksOnClient(Level level, BlockPos pos, Direction facing) {
        PistonStructureResolver resolver = new PistonStructureResolver(level, pos, facing, true);
        if (!resolver.resolve()) return;
        for (BlockPos blockPos : resolver.getToPush()) {
            level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
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

    private static void launchBlocks(ServerLevel level, BlockPos pos, Direction facing) {
        PistonStructureResolver resolver = new PistonStructureResolver(level, pos, facing, true);
        // Too many blocks or something immovable in the way: the piston won't extend, so nothing is launched
        if (!resolver.resolve()) return;

        // Players and mobs standing where the head is about to appear get thrown too. This goes first so the
        // falling blocks spawned below aren't mistaken for entities in the way.
        for (Entity entity : level.getEntities((Entity) null, new AABB(pos.relative(facing)), BouncyPistonBlock::canLaunch)) {
            double lift = facing.getAxis().isHorizontal() ? ENTITY_LAUNCH_LIFT : 0.0;
            entity.setDeltaMovement(facing.getStepX() * ENTITY_LAUNCH_SPEED, facing.getStepY() * ENTITY_LAUNCH_SPEED + lift, facing.getStepZ() * ENTITY_LAUNCH_SPEED);
            entity.hurtMarked = true;
        }

        // Farthest first, so a block never sits in the way of the one behind it
        List<BlockPos> toLaunch = resolver.getToPush();
        for (int i = toLaunch.size() - 1; i >= 0; i--) {
            BlockPos blockPos = toLaunch.get(i);
            BlockState blockState = level.getBlockState(blockPos);
            if (blockState.isAir()) continue;
            FallingBlockEntity block = FallingBlockEntity.fall(level, blockPos, blockState);
            double lift = facing.getAxis().isHorizontal() ? LAUNCH_LIFT : 0.0;
            block.setDeltaMovement(facing.getStepX() * LAUNCH_SPEED, facing.getStepY() * LAUNCH_SPEED + lift, facing.getStepZ() * LAUNCH_SPEED);
            block.hurtMarked = true;
        }
    }

    private static boolean canLaunch(Entity entity) {
        return entity.getPistonPushReaction() != PushReaction.IGNORE && !entity.isSpectator();
    }
}
