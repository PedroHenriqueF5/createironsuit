package dev.createironsuit.energy;

import dev.createironsuit.SuitConstants;
import dev.createironsuit.item.IronSuitArmorItem;
import dev.createironsuit.registry.ModComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class SuitHelper {
    private SuitHelper() {}

    private static final EquipmentSlot[] PIECES = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public static boolean hasFullSuit(LivingEntity entity) {
        for (EquipmentSlot slot : PIECES) {
            if (!(entity.getItemBySlot(slot).getItem() instanceof IronSuitArmorItem)) return false;
        }
        return true;
    }

    public static int getEnergy(ItemStack stack) {
        return stack.getOrDefault(ModComponents.ENERGY.get(), 0);
    }

    public static void setEnergy(ItemStack stack, int value) {
        stack.set(ModComponents.ENERGY.get(), Mth.clamp(value, 0, SuitConstants.MAX_ENERGY));
    }

    /** Retira ate {@code amount} e devolve quanto realmente saiu. */
    public static int extract(ItemStack stack, int amount) {
        int stored = getEnergy(stack);
        int taken = Math.min(stored, amount);
        if (taken > 0) setEnergy(stack, stored - taken);
        return taken;
    }

    /** Coloca ate {@code amount} e devolve quanto realmente entrou. */
    public static int receive(ItemStack stack, int amount) {
        int stored = getEnergy(stack);
        int accepted = Math.min(SuitConstants.MAX_ENERGY - stored, amount);
        if (accepted > 0) setEnergy(stack, stored + accepted);
        return accepted;
    }
}
