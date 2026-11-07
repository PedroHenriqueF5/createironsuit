package dev.createironsuit.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.createironsuit.CreateIronSuit;
import dev.createironsuit.SuitAbilities;
import dev.createironsuit.SuitVariant;
import dev.createironsuit.network.SuitActionPayload;
import dev.createironsuit.registry.ModItems;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

/** Tudo que so existe no cliente: modelos 3D, teclas e envio dos poderes ao servidor. */
@EventBusSubscriber(modid = CreateIronSuit.MODID, value = Dist.CLIENT)
public final class ClientEvents {
    private ClientEvents() {}

    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(CreateIronSuit.MODID, name), "main");
    }

    public static final ModelLayerLocation OUTER = layer("suit_outer");
    public static final ModelLayerLocation INNER = layer("suit_inner");
    public static final ModelLayerLocation HEAVY_OUTER = layer("suit_heavy_outer");
    public static final ModelLayerLocation HEAVY_INNER = layer("suit_heavy_inner");

    private static final String CATEGORY = "key.categories." + CreateIronSuit.MODID;

    private static KeyMapping key(String name, int code) {
        return new KeyMapping("key." + CreateIronSuit.MODID + "." + name, InputConstants.Type.KEYSYM, code, CATEGORY);
    }

    public static final KeyMapping KEY_REPULSOR = key("repulsor", GLFW.GLFW_KEY_R);
    public static final KeyMapping KEY_UNIBEAM = key("unibeam", GLFW.GLFW_KEY_G);
    public static final KeyMapping KEY_PULSE = key("pulse", GLFW.GLFW_KEY_Z);
    public static final KeyMapping KEY_DASH = key("dash", GLFW.GLFW_KEY_V);
    public static final KeyMapping KEY_FLIGHT = key("flight", GLFW.GLFW_KEY_B);
    public static final KeyMapping KEY_SYSTEMS = key("systems", GLFW.GLFW_KEY_H);

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(KEY_REPULSOR);
        event.register(KEY_UNIBEAM);
        event.register(KEY_PULSE);
        event.register(KEY_DASH);
        event.register(KEY_FLIGHT);
        event.register(KEY_SYSTEMS);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(OUTER, () -> SuitArmorModel.createLayer(1.0F, 0.0F));
        event.registerLayerDefinition(INNER, () -> SuitArmorModel.createLayer(0.5F, 0.0F));
        event.registerLayerDefinition(HEAVY_OUTER, () -> SuitArmorModel.createLayer(1.3F, 0.6F));
        event.registerLayerDefinition(HEAVY_INNER, () -> SuitArmorModel.createLayer(0.8F, 0.6F));
    }

    @SubscribeEvent
    public static void registerExtensions(RegisterClientExtensionsEvent event) {
        for (SuitVariant variant : SuitVariant.values()) {
            SuitClientExtensions extensions = new SuitClientExtensions(variant.heavy);
            for (ArmorItem.Type type : new ArmorItem.Type[]{
                    ArmorItem.Type.HELMET, ArmorItem.Type.CHESTPLATE, ArmorItem.Type.LEGGINGS, ArmorItem.Type.BOOTS}) {
                event.registerItem(extensions, ModItems.piece(variant, type).get());
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        boolean canAct = mc.player != null && mc.screen == null;
        send(KEY_REPULSOR, SuitAbilities.REPULSOR, canAct);
        send(KEY_UNIBEAM, SuitAbilities.UNIBEAM, canAct);
        send(KEY_PULSE, SuitAbilities.PULSE, canAct);
        send(KEY_DASH, SuitAbilities.DASH, canAct);
        send(KEY_FLIGHT, SuitAbilities.TOGGLE_FLIGHT, canAct);
        send(KEY_SYSTEMS, SuitAbilities.TOGGLE_SYSTEMS, canAct);
    }

    private static void send(KeyMapping key, int action, boolean canAct) {
        while (key.consumeClick()) {
            if (canAct) PacketDistributor.sendToServer(new SuitActionPayload(action));
        }
    }
}
