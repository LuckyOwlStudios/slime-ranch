package net.luckyowlstudios.slimeranch.item;

import net.luckyowlstudios.slimeranch.entity.Pinky;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;

/**
 * A bucket of milk from a mooshroom. Using it on a slime (any kind, including magma cubes and this
 * mod's slimes) grows it to the next size, up to {@link #MAX_SIZE}. A bigger slime splits into more
 * small slimes when killed, which is what makes it useful for slime farming.
 */
public class MooshroomMilkItem extends Item {

    /** Slimes won't grow past this size. Vanilla slimes spawn at 1, 2 or 4. */
    public static final int MAX_SIZE = 4;

    public MooshroomMilkItem(Properties properties) {
        super(properties);
    }

    /** The size a slime grows to, or 0 if it can't grow. Sizes double: 1, 2, 4. */
    public static int grownSize(Slime slime) {
        if (slime instanceof Pinky) return 0; // Pinky always stays small
        int next = slime.getSize() * 2;
        return next <= MAX_SIZE ? next : 0;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!(target instanceof Slime slime) || !slime.isAlive()) return InteractionResult.PASS;
        int size = grownSize(slime);
        if (size == 0) return InteractionResult.PASS;

        if (player.level() instanceof ServerLevel level) {
            slime.setSize(size, true);
            // Fed slimes shouldn't despawn and take the player's work with them
            slime.setPersistenceRequired();
            level.playSound(null, slime.getX(), slime.getY(), slime.getZ(), SoundEvents.SLIME_SQUISH, SoundSource.NEUTRAL, 1.0F, 0.8F);
            level.playSound(null, slime.getX(), slime.getY(), slime.getZ(), SoundEvents.GENERIC_DRINK, SoundSource.NEUTRAL, 0.8F, 1.0F);
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER, slime.getX(), slime.getY() + slime.getBbHeight() * 0.5, slime.getZ(),
                    6 + size * 3, slime.getBbWidth() * 0.4, slime.getBbHeight() * 0.4, slime.getBbWidth() * 0.4, 0.0);
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BUCKET)));
        }
        return InteractionResult.sidedSuccess(player.level().isClientSide());
    }
}
