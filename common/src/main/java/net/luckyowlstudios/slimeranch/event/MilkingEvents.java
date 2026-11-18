package net.luckyowlstudios.slimeranch.event;

import net.luckyowlstudios.slimeranch.init.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;

/**
 * Milking a mooshroom with a bucket gives Mooshroom Milk instead of regular milk. Cows still give
 * regular milk, and mooshrooms still give stew with a bowl. Called by each loader's entity
 * interaction event before the mooshroom handles the click itself.
 */
public final class MilkingEvents {

    private MilkingEvents() {
    }

    /** Returns the result to use, or {@link InteractionResult#PASS} to let vanilla handle the click. */
    public static InteractionResult onInteractEntity(Player player, InteractionHand hand, Entity entity) {
        if (!(entity instanceof MushroomCow mooshroom) || mooshroom.isBaby() || player.isSpectator()) return InteractionResult.PASS;
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(Items.BUCKET)) return InteractionResult.PASS;

        player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
        player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(ModItems.MOOSHROOM_MILK.get())));
        return InteractionResult.sidedSuccess(player.level().isClientSide());
    }
}
