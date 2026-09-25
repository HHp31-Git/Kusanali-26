package com.kusanali.effect.status_element;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

public class FreezingEffect extends StatusEffect {
    public FreezingEffect() {
        super(StatusEffectCategory.HARMFUL, 0x87CEEB);
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onApplied(entity, attributes, amplifier);
        entity.addStatusEffect(
                new StatusEffectInstance(
                        StatusEffects.SLOWNESS,
                        100,
                        49,
                        false,
                        false,
                        false
                )
        );
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.getWorld() instanceof ServerWorld world) {
            // 生成雪花粒子效果
            double x = entity.getX();
            double y = entity.getY() + entity.getHeight() * 0.5;
            double z = entity.getZ();

            for (int i = 0; i < 3; i++) {
                double offsetX = (entity.getRandom().nextDouble() - 0.5) * 0.6;
                double offsetY = (entity.getRandom().nextDouble() - 0.5) * 0.6;
                double offsetZ = (entity.getRandom().nextDouble() - 0.5) * 0.6;

                world.spawnParticles(
                        ParticleTypes.SNOWFLAKE,
                        x + offsetX,
                        y + offsetY,
                        z + offsetZ,
                        1,
                        0, 0, 0,
                        0.05
                );
            }
        }

        // 限制实体行为
        if (entity instanceof LivingEntity) {
            // 阻止跳跃
            entity.setJumping(false);

            // 阻止攻击
            if (entity.isUsingItem()) {
                entity.stopUsingItem();
            }

            // 阻止物品使用
            if (entity.getActiveItem() != null) {
                entity.clearActiveItem();
            }
        }
    }
}
