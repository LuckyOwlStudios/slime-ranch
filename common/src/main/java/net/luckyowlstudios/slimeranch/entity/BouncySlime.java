package net.luckyowlstudios.slimeranch.entity;

import net.luckyowlstudios.slimeranch.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

/**
 * A blue slime that hops much higher than a vanilla slime and sends whatever it hits flying.
 * Sizes, splitting and damage are the same as a vanilla slime.
 */
public class BouncySlime extends Slime {

    /** How much stronger its hop is than a vanilla slime's; hop height grows with the square of this. */
    public static final float JUMP_MULTIPLIER = 1.7F;
    /** Knockback strength of a hit; a vanilla knockback enchantment level is about 1. */
    public static final double BASE_KNOCKBACK = 1.6;
    /** Extra knockback per size, so the big ones hit harder. */
    public static final double KNOCKBACK_PER_SIZE = 0.15;

    public BouncySlime(EntityType<? extends Slime> type, Level level) {
        super(type, level);
    }

    @Override
    public void jumpFromGround() {
        Vec3 movement = this.getDeltaMovement();
        this.setDeltaMovement(movement.x, this.getJumpPower() * JUMP_MULTIPLIER, movement.z);
        this.hasImpulse = true;
        // Vanilla slimes only puff when they land; this one also bursts off the ground when it jumps
        if (this.level() instanceof ServerLevel level) {
            int size = this.getSize();
            level.sendParticles(this.getParticleType(), this.getX(), this.getY() + 0.1, this.getZ(),
                    4 + size * 4, size * 0.3, 0.05, size * 0.3, 0.08);
        }
    }

    @Override
    protected ParticleOptions getParticleType() {
        // Used for the landing puff too; blue bits of its own slime ball instead of green ones
        return new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(ModItems.BOUNCY_SLIME_BALL.get()));
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        // It lands from its own hops all the time
        return false;
    }

    @Override
    protected void dealDamage(LivingEntity target) {
        super.dealDamage(target);
        // Only knock the target back if the attack actually landed this tick
        boolean hit = target.getLastHurtByMob() == this && target.getLastHurtByMobTimestamp() == target.tickCount;
        if (hit) {
            target.knockback(BASE_KNOCKBACK + KNOCKBACK_PER_SIZE * this.getSize(),
                    this.getX() - target.getX(), this.getZ() - target.getZ());
        }
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() * 1.1F;
    }

    public static boolean checkBouncySlimeSpawnRules(EntityType<BouncySlime> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (level.getDifficulty() == Difficulty.PEACEFUL) return false;
        return Monster.isDarkEnoughToSpawn(level, pos, random)
                && checkMobSpawnRules(type, level, spawnType, pos, random);
    }
}
