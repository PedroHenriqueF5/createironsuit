package dev.createironsuit.registry;

import dev.createironsuit.CreateIronSuit;
import dev.createironsuit.SuitConstants;
import dev.createironsuit.energy.SuitHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
    private ModCreativeTabs() {}

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateIronSuit.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + CreateIronSuit.MODID))
                    .icon(() -> new ItemStack(ModItems.HELMET.get()))
                    .displayItems((params, out) -> {
                        out.accept(ModItems.HELMET.get());
                        ItemStack charged = new ItemStack(ModItems.CHESTPLATE.get());
                        SuitHelper.setEnergy(charged, SuitConstants.MAX_ENERGY);
                        out.accept(charged);
                        out.accept(ModItems.CHESTPLATE.get());
                        out.accept(ModItems.LEGGINGS.get());
                        out.accept(ModItems.BOOTS.get());
                        out.accept(ModItems.REPULSOR_GLOVE.get());
                        out.accept(ModItems.CHARGER.get());
                    })
                    .build());
}
