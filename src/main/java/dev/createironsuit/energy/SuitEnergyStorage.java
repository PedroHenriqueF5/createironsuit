package dev.createironsuit.energy;

import dev.createironsuit.SuitConstants;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;

/** Expoe a energia do componente do item como IEnergyStorage (so recebe; nao deixa sugar de volta). */
public class SuitEnergyStorage implements IEnergyStorage {
    private final ItemStack stack;

    public SuitEnergyStorage(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        if (toReceive <= 0) return 0;
        int space = SuitConstants.MAX_ENERGY - SuitHelper.getEnergy(stack);
        int accepted = Math.min(space, toReceive);
        if (!simulate && accepted > 0) SuitHelper.receive(stack, accepted);
        return accepted;
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return SuitHelper.getEnergy(stack);
    }

    @Override
    public int getMaxEnergyStored() {
        return SuitConstants.MAX_ENERGY;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }
}
