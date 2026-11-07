package dev.createironsuit.block;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import dev.createironsuit.SuitConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class ChargerBlockEntity extends KineticBlockEntity {
    private ItemStack stack = ItemStack.EMPTY;

    public ChargerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public ItemStack getStack() {
        return stack;
    }

    public void setStack(ItemStack newStack) {
        this.stack = newStack;
        setChanged();
        notifyUpdate();
    }

    public ItemStack takeStack() {
        ItemStack out = stack;
        stack = ItemStack.EMPTY;
        setChanged();
        notifyUpdate();
        return out;
    }

    /** O carregador consome stress (por RPM) da rede cinetica. */
    @Override
    public float calculateStressApplied() {
        this.lastStressApplied = SuitConstants.CHARGER_STRESS_IMPACT;
        return lastStressApplied;
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide || stack.isEmpty()) return;

        float speed = Math.abs(getSpeed());
        if (speed <= 0) return;

        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy == null) return;

        int accepted = energy.receiveEnergy(Math.round(speed * SuitConstants.CHARGE_FE_PER_RPM_PER_TICK), false);
        if (accepted > 0 && level.getGameTime() % 20 == 0) {
            setChanged();
            notifyUpdate();
        }
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        if (!stack.isEmpty()) tag.put("Stack", stack.save(registries));
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        stack = tag.contains("Stack")
                ? ItemStack.parse(registries, tag.getCompound("Stack")).orElse(ItemStack.EMPTY)
                : ItemStack.EMPTY;
    }
}
