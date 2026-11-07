package dev.createironsuit.block;

import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import dev.createironsuit.item.IronSuitArmorItem;
import dev.createironsuit.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;

/**
 * Carregador do reator: bloco cinetico do Create. Clique com o peitoral para inserir,
 * de mao vazia para retirar. Com rotacao, carrega o reator (FE).
 */
public class ChargerBlock extends RotatedPillarKineticBlock implements IBE<ChargerBlockEntity> {
    public ChargerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    }

    @Override
    public Class<ChargerBlockEntity> getBlockEntityClass() {
        return ChargerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ChargerBlockEntity> getBlockEntityType() {
        return ModBlockEntities.CHARGER.get();
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                           Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(stack.getItem() instanceof IronSuitArmorItem)
                || stack.getCapability(Capabilities.EnergyStorage.ITEM) == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide) {
            withBlockEntityDo(level, pos, be -> {
                if (be.getStack().isEmpty()) be.setStack(stack.split(1));
            });
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        // so retira com a mao principal vazia (evita tirar a peca ao clicar com outros itens)
        if (!player.getMainHandItem().isEmpty()) return InteractionResult.PASS;
        if (!level.isClientSide) {
            withBlockEntityDo(level, pos, be -> {
                ItemStack out = be.takeStack();
                if (!out.isEmpty()) player.getInventory().placeItemBackInInventory(out);
            });
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock()) && !level.isClientSide) {
            withBlockEntityDo(level, pos, be ->
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), be.getStack()));
        }
        super.onRemove(state, level, pos, newState, moved);
    }
}
