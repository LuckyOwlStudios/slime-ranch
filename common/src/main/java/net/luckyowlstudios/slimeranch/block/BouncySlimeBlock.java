package net.luckyowlstudios.slimeranch.block;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.phys.Vec3;

/**
 * A slime block that bounces entities back up harder than the vanilla one. Falling onto it still
 * cancels fall damage, and sneaking still stops the bounce.
 */
public class BouncySlimeBlock extends SlimeBlock {

    /** Bounce speed relative to the landing speed; vanilla slime blocks use 1.0. */
    public static final double BOUNCE_FACTOR = 1.25;
    /** Bounces never go faster than this (blocks per tick), which is roughly 13 blocks high. */
    public static final double MAX_BOUNCE_SPEED = 1.8;

    public BouncySlimeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter level, Entity entity) {
        if (entity.isSuppressingBounce()) {
            super.updateEntityAfterFallOn(level, entity);
            return;
        }
        Vec3 movement = entity.getDeltaMovement();
        if (movement.y < 0.0) {
            double factor = entity instanceof LivingEntity ? BOUNCE_FACTOR : 1.0;
            entity.setDeltaMovement(movement.x, Math.min(-movement.y * factor, MAX_BOUNCE_SPEED), movement.z);
        }
    }
}
