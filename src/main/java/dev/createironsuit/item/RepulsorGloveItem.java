package dev.createironsuit.item;

import dev.createironsuit.SuitAbilities;
import dev.createironsuit.SuitConstants;
import dev.createironsuit.energy.SuitHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Luva repulsora: segure e clique com o botao direito. Gasta energia do peitoral. */
public class RepulsorGloveItem extends Item {
    public RepulsorGloveItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);

        if (!player.isCreative()) {
            if (!(chest.getItem() instanceof IronSuitArmorItem)) {
                if (!level.isClientSide) {
                    player.displayClientMessage(Component.translatable("message.createironsuit.no_reactor"), true);
                }
                return InteractionResultHolder.fail(held);
            }
            if (SuitHelper.getEnergy(chest) < SuitConstants.REPULSOR_COST) {
                if (!level.isClientSide) {
                    player.displayClientMessage(Component.translatable("message.createironsuit.low_energy"), true);
                }
                return InteractionResultHolder.fail(held);
            }
        }

        if (level instanceof ServerLevel serverLevel) {
            if (!player.isCreative()) SuitHelper.extract(chest, SuitConstants.REPULSOR_COST);
            SuitAbilities.fireBeam(serverLevel, player, SuitConstants.REPULSOR_DAMAGE, SuitConstants.REPULSOR_RANGE,
                    2.0D, SuitConstants.REPULSOR_DAMAGE / 2.0F, false);
        }
        player.getCooldowns().addCooldown(this, SuitConstants.REPULSOR_COOLDOWN_TICKS);
        return InteractionResultHolder.sidedSuccess(held, level.isClientSide());
    }
}
