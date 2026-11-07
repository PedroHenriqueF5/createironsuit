package dev.createironsuit.event;

import dev.createironsuit.CreateIronSuit;
import dev.createironsuit.SuitAbilities;
import dev.createironsuit.SuitConstants;
import dev.createironsuit.SuitVariant;
import dev.createironsuit.energy.SuitHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Habilidades passivas do conjunto completo: efeitos, voo, queda sem dano e painel de status. */
public final class SuitEvents {
    private SuitEvents() {}

    private static final String FLY_TAG = CreateIronSuit.MODID + ":flight";

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        boolean fullSuit = SuitHelper.hasFullSuit(player);
        boolean hasEnergy = fullSuit && SuitHelper.getEnergy(chest) > 0;
        SuitVariant variant = SuitHelper.chestVariant(player);

        handleFlight(player, chest, hasEnergy && SuitAbilities.isFlightEnabled(player), variant);

        if (!hasEnergy) return;

        boolean systems = SuitAbilities.isSystemsEnabled(player);
        if (player.tickCount % 20 == 0 && systems) {
            if (!player.isCreative()) SuitHelper.extract(chest, SuitConstants.IDLE_DRAIN_PER_SECOND);
            applyEffects(player, variant);
        }
        if (player.tickCount % 40 == 0) showStatus(player, chest);
    }

    private static void showStatus(ServerPlayer player, ItemStack chest) {
        int pct = Math.round(100.0F * SuitHelper.getEnergy(chest) / SuitConstants.MAX_ENERGY);
        player.displayClientMessage(Component.translatable("hud.createironsuit.status",
                pct + "%",
                Component.translatable(SuitAbilities.isFlightEnabled(player) ? "hud.createironsuit.on" : "hud.createironsuit.off"),
                Component.translatable(SuitAbilities.isSystemsEnabled(player) ? "hud.createironsuit.on" : "hud.createironsuit.off")),
                true);
    }

    private static void applyEffects(ServerPlayer player, SuitVariant v) {
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, false, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 60, 0, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, v.resistance, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60, v.strength, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, v.speed, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.JUMP, 60, v.jump, false, false, true));
    }

    private static void handleFlight(ServerPlayer player, ItemStack chest, boolean powered, SuitVariant variant) {
        Abilities abilities = player.getAbilities();
        CompoundTag data = player.getPersistentData();

        if (powered) {
            if (!abilities.mayfly) {
                abilities.mayfly = true;
                abilities.setFlyingSpeed(variant.flySpeed);
                data.putBoolean(FLY_TAG, true);
                player.onUpdateAbilities();
            } else if (data.getBoolean(FLY_TAG) && abilities.getFlyingSpeed() != variant.flySpeed) {
                abilities.setFlyingSpeed(variant.flySpeed);
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
                // voo desligado ou sem energia no ar: queda suave por alguns segundos
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
