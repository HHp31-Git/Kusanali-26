package com.kusanali.event.element_reaction;

import com.kusanali.define.event.ElementReaction;
import com.kusanali.entity.ModEntities;
import com.kusanali.entity.custom.DendroSeedEntity;
import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class BoomEvent extends ElementReaction {
    @Override
    protected int getCooldown() { return 20; }

    @Override
    protected List<StatusEffect> getParticipatingEffects() { return List.of(ModEffects.DENDRO, ModEffects.ELECTRO); }

    @Override
    protected void onReact(LivingEntity entity, ServerWorld world, Map<StatusEffect, Long> times) {
        entity.damage(ModDamageTypes.reaction_type_2(world), 2.0f);
        Vec3d center = entity.getPos();

        // 计算圆周上的随机位置
        double angle = world.random.nextDouble() * Math.PI * 2;
        double radius = 1.5;
        double targetX = center.x + Math.cos(angle) * radius;
        double targetZ = center.z + Math.sin(angle) * radius;

        // 找到地面高度
        BlockPos targetPos = BlockPos.ofFloored(targetX, center.y, targetZ);
        BlockState blockState = world.getBlockState(targetPos);
        double groundY = targetPos.getY() + 0.15;

        // 如果圆周位置被方块阻挡，尝试圆内随机位置
        if (!blockState.isAir() && !blockState.getFluidState().isEmpty()) {
            double randomRadius = world.random.nextDouble() * radius;
            double randomAngle = world.random.nextDouble() * Math.PI * 2;
            targetX = center.x + Math.cos(randomAngle) * randomRadius;
            targetZ = center.z + Math.sin(randomAngle) * randomRadius;
            targetPos = BlockPos.ofFloored(targetX, center.y, targetZ);
            groundY = targetPos.getY() + 0.15;
        }

        // 生成草种子
        DendroSeedEntity seed = new DendroSeedEntity(ModEntities.DENDRO_SEED, world);
        seed.setPosition(targetX, groundY, targetZ);
        world.spawnEntity(seed);

        // 粒子效果保持不变
        double cx = center.x, cy = center.y + entity.getHeight() * 0.5, cz = center.z;
        world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, cx, cy, cz, 10, 0.4, 0.4, 0.4, 0.1);
        world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, cx, cy, cz, 6, 0.3, 0.3, 0.3, 0.05);
    }

}
