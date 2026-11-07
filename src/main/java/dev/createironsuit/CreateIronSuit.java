package dev.createironsuit;

import dev.createironsuit.energy.SuitEnergyStorage;
import dev.createironsuit.event.SuitEvents;
import dev.createironsuit.network.SuitActionPayload;
import dev.createironsuit.registry.ModArmorMaterials;
import dev.createironsuit.registry.ModBlockEntities;
import dev.createironsuit.registry.ModBlocks;
import dev.createironsuit.registry.ModComponents;
import dev.createironsuit.registry.ModCreativeTabs;
import dev.createironsuit.registry.ModItems;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

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
        modBus.addListener(this::registerPayloads);

        NeoForge.EVENT_BUS.addListener(SuitEvents::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(SuitEvents::onFall);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Todos os peitorais guardam a energia (FE) do reator
        event.registerItem(Capabilities.EnergyStorage.ITEM,
                (stack, ctx) -> new SuitEnergyStorage(stack),
                ModItems.allChestplates().toArray(new Item[0]));
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(SuitActionPayload.TYPE, SuitActionPayload.STREAM_CODEC, SuitActionPayload::handle);
    }
}
