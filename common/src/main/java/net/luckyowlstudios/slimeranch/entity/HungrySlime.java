package net.luckyowlstudios.slimeranch.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * A red slime that tries to absorb players instead of hurting them. What it does depends on its size:
 * <ul>
 *   <li>Small: nothing. It still chases you but can't hurt you.</li>
 *   <li>Medium: jumps onto your head and clings there, smothering you: your air runs out and then you
 *       take drowning damage. Hitting it knocks it off.</li>
 *   <li>Large (bigger than medium): absorbs you whole. You are held inside it until you kill it.</li>
 * </ul>
 * Players can't be ridden in vanilla, so the clinging and the holding are done by moving the entities
 * every tick instead of by riding.
 */
public class HungrySlime extends Slime {

    public static final int MEDIUM_SIZE = 2;
    /** A medium slime jumps this much harder when it is after a player, so it can reach their head. */
    public static final float MEDIUM_JUMP_MULTIPLIER = 1.55F;
    /** Ticks a smothered player keeps some air for, counting down from the moment the slime lands. */
    public static final int SMOTHER_AIR_TICKS = 60;
    /** Ticks between drowning damage once the air has run out, and how much each hit does. */
    public static final int SMOTHER_DAMAGE_INTERVAL = 20;
    public static final float SMOTHER_DAMAGE = 2.0F;
    /** Ticks a medium slime waits before it can latch on again after being hit off. */
    public static final int REATTACH_COOLDOWN = 80;
    /** Ticks a large slime waits after losing its player, e.g. when the player switches to creative. */
    public static final int ENGULF_COOLDOWN = 100;

    private static final ParticleOptions PARTICLE = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.RED_DYE));

    /** The player this medium slime is clinging to. Not saved: a reloaded slime starts free. */
    @Nullable
    private Player clingingTo;
    /** The player this large slime has absorbed. Not saved either. */
    @Nullable
    private Player absorbed;
    private int smotherTicks;
    private int cooldown;

    public HungrySlime(EntityType<? extends Slime> type, Level level) {
        super(type, level);
    }

    /** It only ever hurts through the abilities below, never by touching. */
    @Override
    protected boolean isDealsDamage() {
        return false;
    }

    @Override
    public void jumpFromGround() {
        Vec3 movement = this.getDeltaMovement();
        float power = this.getJumpPower();
        if (this.getSize() == MEDIUM_SIZE && this.clingingTo == null
                && this.getTarget() instanceof Player target && this.distanceToSqr(target) < 36.0) {
            power *= MEDIUM_JUMP_MULTIPLIER;
        }
        this.setDeltaMovement(movement.x, power, movement.z);
        this.hasImpulse = true;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) return;
        if (this.cooldown > 0) this.cooldown--;

        int size = this.getSize();
        if (size == MEDIUM_SIZE) {
            this.releaseAbsorbed();
            this.tickClinging();
        } else if (size > MEDIUM_SIZE) {
            this.releaseClinging();
            this.tickAbsorbing();
        } else {
            this.releaseClinging();
            this.releaseAbsorbed();
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && this.clingingTo != null) {
            // Getting hit shakes it off
            this.releaseClinging();
            this.cooldown = REATTACH_COOLDOWN;
        }
        return hurt;
    }

    private static boolean canBeTargeted(Player player) {
        return player.isAlive() && !player.isRemoved() && !player.isSpectator() && !player.isCreative();
    }

    private boolean stillHolds(Player player) {
        return canBeTargeted(player) && player.level() == this.level();
    }

    // ---- Medium: clinging to a head and smothering ----

    private void tickClinging() {
        if (this.clingingTo != null) {
            if (!this.stillHolds(this.clingingTo)) {
                this.releaseClinging();
                return;
            }
            Player player = this.clingingTo;
            this.setPos(player.getX(), player.getY() + player.getBbHeight(), player.getZ());
            this.setDeltaMovement(Vec3.ZERO);
            this.fallDistance = 0.0F;
            this.smother(player);
            return;
        }

        // Look for a head to land on: coming down, with its feet around the top of a player's head
        if (this.cooldown > 0 || this.onGround() || this.getDeltaMovement().y > 0.0) return;
        for (Player player : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(0.4, 0.8, 0.4))) {
            if (!canBeTargeted(player) || this.hasSlimeOnHead(player)) continue;
            double headTop = player.getY() + player.getBbHeight();
            double dx = this.getX() - player.getX(), dz = this.getZ() - player.getZ();
            if (this.getY() >= headTop - 0.5 && this.getY() <= headTop + 0.5 && dx * dx + dz * dz < 0.5) {
                this.clingingTo = player;
                this.smotherTicks = 0;
                this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, 0.7F);
                return;
            }
        }
    }

    private boolean hasSlimeOnHead(Player player) {
        for (HungrySlime other : this.level().getEntitiesOfClass(HungrySlime.class, player.getBoundingBox().inflate(1.0, 2.0, 1.0))) {
            if (other != this && other.clingingTo == player) return true;
        }
        return false;
    }

    private void smother(Player player) {
        this.smotherTicks++;
        // The air bar drains over a few seconds, then the player starts to drown
        float airLeft = Math.max(0.0F, 1.0F - (float) this.smotherTicks / SMOTHER_AIR_TICKS);
        player.setAirSupply((int) (player.getMaxAirSupply() * airLeft));
        if (this.smotherTicks > SMOTHER_AIR_TICKS && this.smotherTicks % SMOTHER_DAMAGE_INTERVAL == 0) {
            player.hurt(player.damageSources().drown(), SMOTHER_DAMAGE);
        }
    }

    private void releaseClinging() {
        this.clingingTo = null;
        this.smotherTicks = 0;
    }

    // ---- Large: absorbing a player ----

    private void tickAbsorbing() {
        if (this.absorbed != null) {
            if (!this.stillHolds(this.absorbed)) {
                this.releaseAbsorbed();
                this.cooldown = ENGULF_COOLDOWN;
                return;
            }
            this.keepInside(this.absorbed);
            return;
        }
        if (this.cooldown > 0) return;
        for (Player player : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox())) {
            if (canBeTargeted(player)) {
                this.absorbed = player;
                this.playSound(SoundEvents.SLIME_SQUISH, 1.5F, 0.6F);
                return;
            }
        }
    }

    /** Holds the player in the middle of the slime, moving them along when the slime hops. */
    private void keepInside(Player player) {
        double x = this.getX(), y = this.getY() + 0.1, z = this.getZ();
        if (player.distanceToSqr(x, y, z) > 0.04) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.teleport(x, y, z, player.getYRot(), player.getXRot());
            } else {
                player.setPos(x, y, z);
            }
        }
        player.setDeltaMovement(Vec3.ZERO);
        player.hurtMarked = true;
        player.fallDistance = 0.0F;
    }

    private void releaseAbsorbed() {
        this.absorbed = null;
    }

    // ---- Looks ----

    @Override
    protected ParticleOptions getParticleType() {
        return PARTICLE;
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() * 0.9F;
    }

    /**
     * Spawns where a vanilla slime does: underground in slime chunks, and on the surface in swamps on dark
     * moonlit nights. Biomes where vanilla has no monsters are left out.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean checkHungrySlimeSpawnRules(EntityType<HungrySlime> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (level.getBiome(pos).is(Biomes.MUSHROOM_FIELDS) || level.getBiome(pos).is(Biomes.DEEP_DARK)) return false;
        return Slime.checkSlimeSpawnRules((EntityType) type, level, spawnType, pos, random);
    }
}
