package net.luckyowlstudios.slimeranch.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

/**
 * A small pink slime that hops around very quickly and takes a lot of hits to kill. It is always
 * the smallest slime size, so it never splits, and it doesn't hunt players or deal damage.
 */
public class Pinky extends Slime {

    public static final int SIZE = 1;
    public static final double MAX_HEALTH = 30.0;
    public static final double MOVEMENT_SPEED = 0.5;
    /** Ticks between hops; vanilla slimes wait 10 to 29. */
    public static final int MIN_JUMP_DELAY = 2, MAX_JUMP_DELAY = 6;
    /** Experience dropped; a vanilla small slime drops 1. */
    public static final int XP_REWARD = 5;

    private static final ParticleOptions PARTICLE = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.PINK_DYE));

    public Pinky(EntityType<? extends Slime> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // Keep the hopping and wandering goals, drop the ones that make it chase players and golems
        this.targetSelector.removeAllGoals(goal -> true);
    }

    @Override
    public void setSize(int size, boolean resetHealth) {
        // Ignore the size from spawning, commands and saves: Pinky is always small
        super.setSize(SIZE, resetHealth);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(MAX_HEALTH);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(MOVEMENT_SPEED);
        this.xpReward = XP_REWARD;
        if (resetHealth) {
            this.setHealth(this.getMaxHealth());
        }
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        this.setSize(SIZE, true);
        return data;
    }

    @Override
    protected int getJumpDelay() {
        return MIN_JUMP_DELAY + this.random.nextInt(MAX_JUMP_DELAY - MIN_JUMP_DELAY + 1);
    }

    @Override
    protected ParticleOptions getParticleType() {
        return PARTICLE;
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() * 1.3F;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        // Spawns like an animal, so it stays around like one
        return false;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    public static boolean checkPinkySpawnRules(EntityType<Pinky> type, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON)
                && level.getRawBrightness(pos, 0) > 8;
    }
}
