package dev.createironsuit.item;

import dev.createironsuit.SuitConstants;
import dev.createironsuit.SuitVariant;
import dev.createironsuit.energy.SuitHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class IronSuitArmorItem extends ArmorItem {
    private final SuitVariant variant;

    public IronSuitArmorItem(Holder<ArmorMaterial> material, Type type, SuitVariant variant, Item.Properties properties) {
        super(material, type, properties);
        this.variant = variant;
    }

    public SuitVariant getVariant() {
        return variant;
    }

    private boolean isReactor() {
        return getType() == Type.CHESTPLATE;
    }

    // No peitoral, a barrinha do item mostra a carga do reator
    @Override
    public boolean isBarVisible(ItemStack stack) {
        return isReactor() || super.isBarVisible(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (!isReactor()) return super.getBarWidth(stack);
        return Math.round(13.0F * SuitHelper.getEnergy(stack) / SuitConstants.MAX_ENERGY);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return isReactor() ? 0x2EC4FF : super.getBarColor(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        if (isReactor()) {
            tooltip.add(Component.translatable("tooltip.createironsuit.energy",
                    String.format("%,d", SuitHelper.getEnergy(stack)),
                    String.format("%,d", SuitConstants.MAX_ENERGY)).withStyle(ChatFormatting.AQUA));
            tooltip.add(Component.translatable("tooltip.createironsuit.charge_hint").withStyle(ChatFormatting.GRAY));
        }
        tooltip.add(Component.translatable("tooltip.createironsuit.full_set").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("tooltip.createironsuit.keys").withStyle(ChatFormatting.DARK_GRAY));
    }
}
