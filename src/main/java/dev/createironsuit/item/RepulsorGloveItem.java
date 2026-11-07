package dev.createironsuit.item;

import dev.createironsuit.SuitConstants;
import dev.createironsuit.energy.SuitHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

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
            fire(serverLevel, player);
        }
        player.getCooldowns().addCooldown(this, SuitConstants.REPULSOR_COOLDOWN_TICKS);
        return InteractionResultHolder.sidedSuccess(held, level.isClientSide());
    }

    private void fire(ServerLevel level, Player player) {
        Vec3 start = player.getEyePosition();
        Vec3 dir = player.getLookAngle();
        Vec3 end = start.add(dir.scale(SuitConstants.REPULSOR_RANGE));

        BlockHitResult blockHit = level.clip(new ClipContext(start, end,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        Vec3 limit = blockHit.getType() == HitResult.Type.MISS ? end : blockHit.getLocation();

        AABB box = player.getBoundingBox().expandTowards(dir.scale(SuitConstants.REPULSOR_RANGE)).inflate(1.0D);
        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(player, start, limit, box,
                e -> !e.isSpectator() && e.isPickable() && e != player,
                start.distanceToSqr(limit));

        Vec3 impact = limit;
        if (entityHit != null) {
            impact = entityHit.getLocation();
            Entity target = entityHit.getEntity();
            target.hurt(level.damageSources().playerAttack(player), SuitConstants.REPULSOR_DAMAGE);
        }

        // raio de particulas
        double dist = start.distanceTo(impact);
        for (double d = 1.0D; d < dist; d += 0.5D) {
            Vec3 p = start.add(dir.scale(d));
            level.sendParticles(ParticleTypes.END_ROD, p.x, p.y - 0.15D, p.z, 1, 0, 0, 0, 0.0D);
        }
        level.sendParticles(ParticleTypes.EXPLOSION, impact.x, impact.y, impact.z, 1, 0, 0, 0, 0.0D);

        // dano em area pequeno no impacto
        AABB splash = new AABB(impact, impact).inflate(2.0D);
        for (LivingEntity victim : level.getEntitiesOfClass(LivingEntity.class, splash, e -> e != player)) {
            if (entityHit != null && victim == entityHit.getEntity()) continue;
            victim.hurt(level.damageSources().playerAttack(player), SuitConstants.REPULSOR_DAMAGE / 2.0F);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 1.0F, 0.6F);
    }
}
