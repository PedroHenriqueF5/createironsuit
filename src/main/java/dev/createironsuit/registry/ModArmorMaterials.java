package dev.createironsuit.registry;

import dev.createironsuit.CreateIronSuit;
import java.util.EnumMap;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModArmorMaterials {
    private ModArmorMaterials() {}

    public static final DeferredRegister<ArmorMaterial> MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, CreateIronSuit.MODID);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> IRON_SUIT = MATERIALS.register("iron_suit",
            () -> new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                        map.put(ArmorItem.Type.BOOTS, 3);
                        map.put(ArmorItem.Type.LEGGINGS, 6);
                        map.put(ArmorItem.Type.CHESTPLATE, 8);
                        map.put(ArmorItem.Type.HELMET, 3);
                        map.put(ArmorItem.Type.BODY, 11);
                    }),
                    25,
                    SoundEvents.ARMOR_EQUIP_NETHERITE,
                    () -> Ingredient.of(Items.IRON_INGOT),
                    // texturas: textures/models/armor/iron_suit_layer_1.png e _layer_2.png
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(CreateIronSuit.MODID, "iron_suit"))),
                    3.0F,
                    0.1F));
}
