package dev.createironsuit.registry;

import com.mojang.serialization.Codec;
import dev.createironsuit.CreateIronSuit;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModComponents {
    private ModComponents() {}

    public static final DeferredRegister<DataComponentType<?>> COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, CreateIronSuit.MODID);

    /** Energia (FE) armazenada no item. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY =
            COMPONENTS.register("energy", () -> DataComponentType.<Integer>builder()
                    .persistent(ExtraCodecs.NON_NEGATIVE_INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
                    .build());
}
