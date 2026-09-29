package com.kusanali.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;

public class TribbleEffect extends StatusEffect {
    // 蕴种印
    public TribbleEffect() {
        super(StatusEffectCategory.HARMFUL, 0x00FF00);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // 每 2 tick 触发一次，平衡粒子密度与性能
        return duration % 2 == 0;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.getWorld().isClient()) return;

        ServerWorld world = (ServerWorld) entity.getWorld();
        double centerX = entity.getX();
        double centerY = entity.getY() + entity.getHeight() * 0.5; // 身体中心
        double centerZ = entity.getZ();

        // 将实体的身体朝向转换为弧度
        float bodyYawRad = entity.bodyYaw * MathHelper.RADIANS_PER_DEGREE;

        // 环绕半径与旋转速度
        double radius = 0.6;
        double speed = 0.15;

        // 时间驱动旋转角 (利用 age 保证持续旋转)
        double time = entity.age * speed;

        // --- 第一圈光环 ---
        for (int i = 0; i < 2; i++) {
            double angle = time + (Math.PI * i); // 对称两点成环
            double offsetX = Math.cos(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;

            // 旋转矩阵：将粒子偏移对齐实体朝向
            double rotatedX = offsetX * Math.cos(bodyYawRad) - offsetZ * Math.sin(bodyYawRad);
            double rotatedZ = offsetX * Math.sin(bodyYawRad) + offsetZ * Math.cos(bodyYawRad);

            world.spawnParticles(
                    ParticleTypes.HAPPY_VILLAGER, // 绿色粒子
                    centerX + rotatedX, centerY, centerZ + rotatedZ,
                    1, 0, 0, 0, 0.01
            );
        }

        // --- 第二圈光环 (X型交叉：偏移45度，垂直高度错开) ---
        double crossOffset = Math.PI / 4; // 45度
        double heightDiff = 0.15;         // 高度差

        for (int i = 0; i < 2; i++) {
            double angle = time + crossOffset + (Math.PI * i);
            double offsetX = Math.cos(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;

            double rotatedX = offsetX * Math.cos(bodyYawRad) - offsetZ * Math.sin(bodyYawRad);
            double rotatedZ = offsetX * Math.sin(bodyYawRad) + offsetZ * Math.cos(bodyYawRad);

            world.spawnParticles(
                    ParticleTypes.HAPPY_VILLAGER,
                    centerX + rotatedX, centerY + heightDiff, centerZ + rotatedZ,
                    1, 0, 0, 0, 0.01
            );
        }
    }
}
