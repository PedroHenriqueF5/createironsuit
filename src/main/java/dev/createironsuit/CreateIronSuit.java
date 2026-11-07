package dev.createironsuit;

import dev.createironsuit.energy.SuitEnergyStorage;
import dev.createironsuit.event.SuitEvents;
import dev.createironsuit.registry.ModArmorMaterials;
import dev.createironsuit.registry.ModBlockEntities;
import dev.createironsuit.registry.ModBlocks;
import dev.createironsuit.registry.ModComponents;
import dev.createironsuit.registry.ModCreativeTabs;
import dev.createironsuit.registry.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(CreateIronSuit.MODID)
public class CreateIronSuit {
    public static final String MODID = "createironsuit";

    public CreateIronSuit(IEventBus modBus) {
        ModComponents.COMPONENTS.register(modBus);
        ModArmorMaterials.MATERIALS.register(modBus);
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModCreativeTabs.TABS.register(modBus);

        modBus.addListener(this::registerCapabilities);

        NeoForge.EVENT_BUS.addListener(SuitEvents::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(SuitEvents::onFall);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        // O peitoral guarda a energia (FE) do reator
        event.registerItem(Capabilities.EnergyStorage.ITEM,
                (stack, ctx) -> new SuitEnergyStorage(stack),
                ModItems.CHESTPLATE.get());
    }
}
