package dev.createironsuit.registry;

import dev.createironsuit.CreateIronSuit;
import dev.createironsuit.SuitVariant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
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

    public static final Map<SuitVariant, DeferredHolder<ArmorMaterial, ArmorMaterial>> BY_VARIANT =
            new EnumMap<>(SuitVariant.class);

    static {
        for (SuitVariant variant : SuitVariant.values()) {
            BY_VARIANT.put(variant, MATERIALS.register(variant.id, () -> create(variant)));
        }
    }

    private static ArmorMaterial create(SuitVariant v) {
        return new ArmorMaterial(
                Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                    map.put(ArmorItem.Type.BOOTS, v.bootsDefense);
                    map.put(ArmorItem.Type.LEGGINGS, v.leggingsDefense);
                    map.put(ArmorItem.Type.CHESTPLATE, v.chestDefense);
                    map.put(ArmorItem.Type.HELMET, v.helmetDefense);
                    map.put(ArmorItem.Type.BODY, v.chestDefense + 3);
                }),
                25,
                SoundEvents.ARMOR_EQUIP_NETHERITE,
                () -> Ingredient.of(Items.IRON_INGOT),
                // texturas: textures/models/armor/<id>_layer_1.png e _layer_2.png
                List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(CreateIronSuit.MODID, v.id))),
                v.toughness,
                v.knockbackResistance);
    }
}
