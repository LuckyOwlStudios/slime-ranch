package net.luckyowlstudios.slimeranch.init;

import net.luckyowlstudios.slimeranch.block.BouncyPistonBlock;
import net.luckyowlstudios.slimeranch.block.BouncySlimeBlock;
import net.luckyowlstudios.slimeranch.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Supplier;

public class ModBlocks {

    public static final Supplier<BouncySlimeBlock> BOUNCY_SLIME_BLOCK = Services.PLATFORM.register(BuiltInRegistries.BLOCK, "bouncy_slime_block",
            () -> new BouncySlimeBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .friction(0.8F)
                    .sound(SoundType.SLIME_BLOCK)
                    .noOcclusion()));

    // The same properties as a vanilla piston
    public static final Supplier<BouncyPistonBlock> BOUNCY_PISTON = Services.PLATFORM.register(BuiltInRegistries.BLOCK, "bouncy_piston",
            () -> new BouncyPistonBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(1.5F)
                    .isRedstoneConductor((state, level, pos) -> false)
                    .isSuffocating((state, level, pos) -> !state.getValue(PistonBaseBlock.EXTENDED))
                    .isViewBlocking((state, level, pos) -> !state.getValue(PistonBaseBlock.EXTENDED))));

    public static void init() {
    }
}
