package dev.createironsuit;

import dev.createironsuit.energy.SuitHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Logica (no servidor) de todos os poderes ativados por tecla. */
public final class SuitAbilities {
    private SuitAbilities() {}

    public static final int REPULSOR = 0;
    public static final int UNIBEAM = 1;
    public static final int PULSE = 2;
    public static final int DASH = 3;
    public static final int TOGGLE_FLIGHT = 4;
    public static final int TOGGLE_SYSTEMS = 5;

    private static final String FLIGHT_OFF = CreateIronSuit.MODID + ":flight_off";
    private static final String SYSTEMS_OFF = CreateIronSuit.MODID + ":systems_off";

    public static boolean isFlightEnabled(Player p) {
        return !p.getPersistentData().getBoolean(FLIGHT_OFF);
    }

    public static boolean isSystemsEnabled(Player p) {
        return !p.getPersistentData().getBoolean(SYSTEMS_OFF);
    }

    private static void msg(ServerPlayer p, String key) {
        p.displayClientMessage(Component.translatable("message." + CreateIronSuit.MODID + "." + key), true);
    }

    private static boolean ready(ServerPlayer p, int action) {
        return p.level().getGameTime() >= p.getPersistentData().getLong(CreateIronSuit.MODID + ":cd" + action);
    }

    private static void startCooldown(ServerPlayer p, int action, int ticks) {
        p.getPersistentData().putLong(CreateIronSuit.MODID + ":cd" + action, p.level().getGameTime() + ticks);
    }

    /** Ponto de entrada: chamado quando o pacote de tecla chega ao servidor. */
    public static void perform(ServerPlayer p, int action) {
        if (!SuitHelper.hasFullSuit(p)) {
            msg(p, "need_suit");
            return;
        }
        ItemStack chest = p.getItemBySlot(EquipmentSlot.CHEST);
        CompoundTag data = p.getPersistentData();

        switch (action) {
            case TOGGLE_FLIGHT -> {
                boolean nowOff = !data.getBoolean(FLIGHT_OFF);
                data.putBoolean(FLIGHT_OFF, nowOff);
                msg(p, nowOff ? "flight_off" : "flight_on");
            }
            case TOGGLE_SYSTEMS -> {
                boolean nowOff = !data.getBoolean(SYSTEMS_OFF);
                data.putBoolean(SYSTEMS_OFF, nowOff);
                if (nowOff) p.removeEffect(MobEffects.NIGHT_VISION);
                msg(p, nowOff ? "systems_off" : "systems_on");
            }
            case REPULSOR -> {
                if (!ready(p, action)) return;
                if (!spend(p, chest, SuitConstants.REPULSOR_COST)) return;
                startCooldown(p, action, SuitConstants.REPULSOR_KEY_COOLDOWN_TICKS);
                fireBeam((ServerLevel) p.level(), p, SuitConstants.REPULSOR_DAMAGE, SuitConstants.REPULSOR_RANGE,
                        2.0D, SuitConstants.REPULSOR_DAMAGE / 2.0F, false);
            }
            case UNIBEAM -> {
                if (!ready(p, action)) return;
                if (!spend(p, chest, SuitConstants.UNIBEAM_COST)) return;
                startCooldown(p, action, SuitConstants.UNIBEAM_COOLDOWN_TICKS);
                fireBeam((ServerLevel) p.level(), p, SuitConstants.UNIBEAM_DAMAGE, SuitConstants.UNIBEAM_RANGE,
                        4.0D, SuitConstants.UNIBEAM_DAMAGE / 2.0F, true);
            }
            case PULSE -> {
                if (!ready(p, action)) return;
                if (!spend(p, chest, SuitConstants.PULSE_COST)) return;
                startCooldown(p, action, SuitConstants.PULSE_COOLDOWN_TICKS);
                pulse((ServerLevel) p.level(), p);
            }
            case DASH -> {
                if (!ready(p, action)) return;
                if (!spend(p, chest, SuitConstants.DASH_COST)) return;
                startCooldown(p, action, SuitConstants.DASH_COOLDOWN_TICKS);
                dash((ServerLevel) p.level(), p);
            }
            default -> { }
        }
    }

    /** Gasta energia do reator (de graca no criativo). Avisa se nao houver o suficiente. */
    private static boolean spend(ServerPlayer p, ItemStack chest, int cost) {
        if (p.isCreative()) return true;
        if (SuitHelper.getEnergy(chest) < cost) {
            msg(p, "low_energy");
            return false;
        }
        SuitHelper.extract(chest, cost);
        return true;
    }

    // ------------------------------------------------------------------
    // Raio (repulsor e unibeam). Usado tambem pela Luva Repulsora.
    // ------------------------------------------------------------------
    public static void fireBeam(ServerLevel level, Player player, float damage, double range,
                                double splashRadius, float splashDamage, boolean heavy) {
        Vec3 start = player.getEyePosition();
        Vec3 dir = player.getLookAngle();
        Vec3 end = start.add(dir.scale(range));

        BlockHitResult blockHit = level.clip(new ClipContext(start, end,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        Vec3 limit = blockHit.getType() == HitResult.Type.MISS ? end : blockHit.getLocation();

        AABB box = player.getBoundingBox().expandTowards(dir.scale(range)).inflate(1.0D);
        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(player, start, limit, box,
                e -> !e.isSpectator() && e.isPickable() && e != player,
                start.distanceToSqr(limit));

        Vec3 impact = limit;
        Entity direct = null;
        if (entityHit != null) {
            impact = entityHit.getLocation();
            direct = entityHit.getEntity();
            direct.hurt(level.damageSources().playerAttack(player), damage);
        }

        double step = heavy ? 0.3D : 0.5D;
        double dist = start.distanceTo(impact);
        for (double d = 1.0D; d < dist; d += step) {
            Vec3 p = start.add(dir.scale(d));
            level.sendParticles(ParticleTypes.END_ROD, p.x, p.y - 0.15D, p.z, heavy ? 2 : 1,
                    heavy ? 0.08D : 0.0D, heavy ? 0.08D : 0.0D, heavy ? 0.08D : 0.0D, 0.0D);
            if (heavy) level.sendParticles(ParticleTypes.ELECTRIC_SPARK, p.x, p.y - 0.15D, p.z, 1, 0.1D, 0.1D, 0.1D, 0.0D);
        }
        level.sendParticles(heavy ? ParticleTypes.EXPLOSION_EMITTER : ParticleTypes.EXPLOSION,
                impact.x, impact.y, impact.z, 1, 0, 0, 0, 0.0D);

        AABB splash = new AABB(impact, impact).inflate(splashRadius);
        for (LivingEntity victim : level.getEntitiesOfClass(LivingEntity.class, splash, e -> e != player)) {
            if (victim == direct) continue;
            victim.hurt(level.damageSources().playerAttack(player), splashDamage);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                heavy ? SoundEvents.DRAGON_FIREBALL_EXPLODE : SoundEvents.FIREWORK_ROCKET_BLAST,
                SoundSource.PLAYERS, heavy ? 1.2F : 1.0F, heavy ? 0.5F : 0.6F);
    }

    // ------------------------------------------------------------------
    // Pulso de energia: empurra e fere tudo ao redor.
    // ------------------------------------------------------------------
    private static void pulse(ServerLevel level, ServerPlayer p) {
        double r = SuitConstants.PULSE_RADIUS;
        AABB box = p.getBoundingBox().inflate(r);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box,
                x -> x != p && x.distanceToSqr(p) <= r * r)) {
            Vec3 away = new Vec3(e.getX() - p.getX(), 0, e.getZ() - p.getZ()).normalize();
            e.hurt(level.damageSources().playerAttack(p), SuitConstants.PULSE_DAMAGE);
            e.push(away.x * 1.6D, 0.6D, away.z * 1.6D);
            e.hurtMarked = true;
        }
        for (double ring = 1.0D; ring <= r; ring += 1.5D) {
            for (int a = 0; a < 360; a += 24) {
                double rad = Math.toRadians(a);
                level.sendParticles(ParticleTypes.CLOUD,
                        p.getX() + Math.cos(rad) * ring, p.getY() + 0.2D, p.getZ() + Math.sin(rad) * ring,
                        1, 0, 0, 0, 0.0D);
            }
        }
        level.sendParticles(ParticleTypes.SONIC_BOOM, p.getX(), p.getY() + 1.0D, p.getZ(), 1, 0, 0, 0, 0.0D);
        level.playSound(null, p.getX(), p.getY(), p.getZ(), SoundEvents.DRAGON_FIREBALL_EXPLODE,
                SoundSource.PLAYERS, 1.0F, 1.4F);
    }

    // ------------------------------------------------------------------
    // Impulso: arranque rapido na direcao em que voce olha.
    // ------------------------------------------------------------------
    private static void dash(ServerLevel level, ServerPlayer p) {
        Vec3 look = p.getLookAngle();
        p.setDeltaMovement(look.scale(SuitConstants.DASH_SPEED));
        p.hurtMarked = true;
        p.fallDistance = 0.0F;
        level.sendParticles(ParticleTypes.FLAME, p.getX(), p.getY() + 0.4D, p.getZ(), 20, 0.3D, 0.3D, 0.3D, 0.05D);
        level.sendParticles(ParticleTypes.CLOUD, p.getX(), p.getY() + 0.4D, p.getZ(), 10, 0.3D, 0.3D, 0.3D, 0.05D);
        level.playSound(null, p.getX(), p.getY(), p.getZ(), SoundEvents.FIREWORK_ROCKET_LAUNCH,
                SoundSource.PLAYERS, 1.0F, 0.8F);
    }
}
