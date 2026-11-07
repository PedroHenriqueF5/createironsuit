package dev.createironsuit.registry;

import dev.createironsuit.CreateIronSuit;
import dev.createironsuit.block.ChargerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    private ModBlocks() {}

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CreateIronSuit.MODID);

    public static final DeferredBlock<ChargerBlock> CHARGER = BLOCKS.register("arc_charger",
            () -> new ChargerBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.5F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()));
}
