package net.luckyowlstudios.slimeranch.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biomes;

/**
 * A gray, faceless slime that lives in caves. It behaves like a vanilla slime (same sizes, splitting
 * and attacks) but has more health at every size.
 */
public class CaveSlime extends Slime {

    /** Health compared to a vanilla slime of the same size. */
    public static final float HEALTH_MULTIPLIER = 1.5F;
    /** Cave slimes only spawn naturally below this height. */
    public static final int MAX_SPAWN_Y = 50;

    private static final ParticleOptions PARTICLE = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.GRAY_DYE));

    public CaveSlime(EntityType<? extends Slime> type, Level level) {
        super(type, level);
    }

    @Override
    public void setSize(int size, boolean resetHealth) {
        super.setSize(size, resetHealth);
        // Vanilla sets max health to size squared (1, 4 or 16); round up so the small one has 2
        int size2 = this.getSize();
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(Math.ceil(size2 * size2 * HEALTH_MULTIPLIER));
        if (resetHealth) {
            this.setHealth(this.getMaxHealth());
        }
    }

    @Override
    protected ParticleOptions getParticleType() {
        return PARTICLE;
    }

    @Override
    public float getVoicePitch() {
        // A little deeper than a vanilla slime
        return super.getVoicePitch() * 0.85F;
    }

    public static boolean checkCaveSlimeSpawnRules(EntityType<CaveSlime> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (level.getDifficulty() == Difficulty.PEACEFUL) return false;
        if (MobSpawnType.isSpawner(spawnType)) return checkMobSpawnRules(type, level, spawnType, pos, random);
        return pos.getY() < MAX_SPAWN_Y
                && !level.canSeeSky(pos)
                && !level.getBiome(pos).is(Biomes.MUSHROOM_FIELDS)
                && !level.getBiome(pos).is(Biomes.DEEP_DARK)
                && Monster.isDarkEnoughToSpawn(level, pos, random)
                && checkMobSpawnRules(type, level, spawnType, pos, random);
    }
}
