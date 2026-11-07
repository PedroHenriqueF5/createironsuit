package dev.createironsuit.event;

import dev.createironsuit.CreateIronSuit;
import dev.createironsuit.SuitConstants;
import dev.createironsuit.energy.SuitHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Habilidades do conjunto completo: efeitos, voo e queda sem dano. Tudo no servidor. */
public final class SuitEvents {
    private SuitEvents() {}

    private static final String FLY_TAG = CreateIronSuit.MODID + ":flight";

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        boolean powered = SuitHelper.hasFullSuit(player) && SuitHelper.getEnergy(chest) > 0;

        handleFlight(player, chest, powered);
        if (!powered) return;

        if (player.tickCount % 20 == 0) {
            if (!player.isCreative()) SuitHelper.extract(chest, SuitConstants.IDLE_DRAIN_PER_SECOND);
            applyEffects(player);
        }
    }

    private static void applyEffects(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, false, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 1, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60, 1, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.JUMP, 60, 1, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 0, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 60, 0, false, false, true));
    }

    private static void handleFlight(ServerPlayer player, ItemStack chest, boolean powered) {
        Abilities abilities = player.getAbilities();
        CompoundTag data = player.getPersistentData();

        if (powered) {
            if (!abilities.mayfly) {
                abilities.mayfly = true;
                abilities.setFlyingSpeed(SuitConstants.FLY_SPEED);
                data.putBoolean(FLY_TAG, true);
                player.onUpdateAbilities();
            }
            if (data.getBoolean(FLY_TAG) && abilities.flying && !player.onGround() && !player.isCreative()) {
                SuitHelper.extract(chest, SuitConstants.FLIGHT_DRAIN_PER_TICK);
            }
        } else if (data.getBoolean(FLY_TAG)) {
            data.remove(FLY_TAG);
            boolean wasFlying = abilities.flying;
            if (!player.isCreative() && !player.isSpectator()) {
                abilities.mayfly = false;
                abilities.flying = false;
            }
            abilities.setFlyingSpeed(SuitConstants.DEFAULT_FLY_SPEED);
            player.onUpdateAbilities();
            if (wasFlying && !player.isCreative()) {
                // acabou a energia no ar: queda suave por alguns segundos
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 160, 0, false, false, true));
            }
        }
    }

    public static void onFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player
                && SuitHelper.hasFullSuit(player)
                && SuitHelper.getEnergy(player.getItemBySlot(EquipmentSlot.CHEST)) > 0) {
            event.setDamageMultiplier(0.0F);
        }
    }
}
