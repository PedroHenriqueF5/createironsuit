package dev.createironsuit.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

/** Troca o modelo vanilla da armadura pelo modelo 3D do mod. */
public class SuitClientExtensions implements IClientItemExtensions {
    private final ModelLayerLocation outerLayer;
    private final ModelLayerLocation innerLayer;

    private EntityModelSet cachedSet;
    private SuitArmorModel outer;
    private SuitArmorModel inner;

    public SuitClientExtensions(boolean heavy) {
        this.outerLayer = heavy ? ClientEvents.HEAVY_OUTER : ClientEvents.OUTER;
        this.innerLayer = heavy ? ClientEvents.HEAVY_INNER : ClientEvents.INNER;
    }

    @Override
    public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack,
                                                  EquipmentSlot slot, HumanoidModel<?> original) {
        EntityModelSet set = Minecraft.getInstance().getEntityModels();
        if (set != cachedSet) { // recarregou os recursos: refaz os modelos
            cachedSet = set;
            outer = null;
            inner = null;
        }
        SuitArmorModel model;
        if (slot == EquipmentSlot.LEGS) {
            if (inner == null) inner = new SuitArmorModel(set.bakeLayer(innerLayer));
            model = inner;
        } else {
            if (outer == null) outer = new SuitArmorModel(set.bakeLayer(outerLayer));
            model = outer;
        }
        model.setSlot(slot);
        return model;
    }
}
