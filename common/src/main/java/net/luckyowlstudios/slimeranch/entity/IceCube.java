package net.luckyowlstudios.slimeranch.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

/**
 * An icy slime from snowy biomes. It behaves like a vanilla slime, but every hit it lands chills the
 * target the way powder snow does: a few hits freeze it solid, slowing it down and dealing freeze
 * damage until it thaws. Leather armor protects against it, as it does against powder snow.
 */
public class IceCube extends Slime {

    /** Freeze ticks added per hit. 140 ticks is fully frozen, and frozen entities thaw 2 ticks per tick. */
    public static final int FREEZE_TICKS_PER_HIT = 70;
    /** How far past fully frozen the hits can push the target, so it stays frozen for a moment. */
    public static final int MAX_EXTRA_FREEZE_TICKS = 80;

    public IceCube(EntityType<? extends Slime> type, Level level) {
        super(type, level);
    }

    @Override
    protected void dealDamage(LivingEntity target) {
        super.dealDamage(target);
        // Only chill the target if the attack actually landed this tick
        boolean hit = target.getLastHurtByMob() == this && target.getLastHurtByMobTimestamp() == target.tickCount;
        if (hit && target.canFreeze()) {
            int max = target.getTicksRequiredToFreeze() + MAX_EXTRA_FREEZE_TICKS;
            target.setTicksFrozen(Math.min(max, target.getTicksFrozen() + FREEZE_TICKS_PER_HIT));
        }
    }

    @Override
    public boolean canFreeze() {
        return false;
    }

    @Override
    protected ParticleOptions getParticleType() {
        return ParticleTypes.ITEM_SNOWBALL;
    }

    @Override
    public float getVoicePitch() {
        // A little higher and crisper than a vanilla slime
        return super.getVoicePitch() * 1.15F;
    }

    public static boolean checkIceCubeSpawnRules(EntityType<IceCube> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (level.getDifficulty() == Difficulty.PEACEFUL) return false;
        return Monster.isDarkEnoughToSpawn(level, pos, random)
                && checkMobSpawnRules(type, level, spawnType, pos, random);
    }
}
