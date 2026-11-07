package dev.createironsuit.registry;

import dev.createironsuit.CreateIronSuit;
import dev.createironsuit.block.ChargerBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
    private ModBlockEntities() {}

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CreateIronSuit.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChargerBlockEntity>> CHARGER =
            BLOCK_ENTITIES.register("arc_charger", () -> BlockEntityType.Builder
                    .of((pos, state) -> new ChargerBlockEntity(ModBlockEntities.CHARGER.get(), pos, state),
                            ModBlocks.CHARGER.get())
                    .build(null));
}
