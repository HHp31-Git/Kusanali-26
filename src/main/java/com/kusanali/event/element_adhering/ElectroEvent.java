package com.kusanali.event.element_adhering;

import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

public class ElectroEvent {

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity.getWorld().isClient()) return true;

            if (!source.isOf(DamageTypes.LIGHTNING_BOLT)) return true;

            if (!(entity.getWorld() instanceof ServerWorld serverWorld)) return true;

            // 以被雷劈的实体为中心，半径 1.5 格
            Box area = new Box(
                    entity.getX() - 1.5,
                    entity.getY() - 1.5,
                    entity.getZ() - 1.5,
                    entity.getX() + 1.5,
                    entity.getY() + 1.5,
                    entity.getZ() + 1.5
            );

// 使用批处理优化效果添加
            serverWorld.getEntitiesByClass(
                            LivingEntity.class,
                            area,
                            e -> e.isAlive() && e != entity
                    ).stream()
                    .filter(target -> {
                        StatusEffectInstance existing = target.getStatusEffect(ModEffects.ELECTRO);
                        return existing == null || existing.getDuration() <= 160;
                    })
                    .forEach(target -> target.addStatusEffect(new StatusEffectInstance(
                            ModEffects.ELECTRO,
                            200,
                            0,
                            false,
                            true,
                            true
                    )));

            return true;
        });
    }
}
