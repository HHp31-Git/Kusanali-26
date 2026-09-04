package com.kusanali.event.element_adhering;

import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.util.math.Box;

import java.util.List;

public class CyroSnowEvent {

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            world.getTime();

            if (world.getTime() % 2 != 0) return;

            for (SnowballEntity entity : world.getEntitiesByClass(
                    SnowballEntity.class,
                    new Box(-50, -50, -50, 50, 50, 50),
                    e -> true
            )) {

                // 使用更精确的碰撞检测
                Box box = entity.getBoundingBox().expand(0.2);

                // 批量处理命中目标
                List<LivingEntity> targets = world.getEntitiesByClass(
                        LivingEntity.class,
                        box,
                        target -> target != entity.getOwner() && target.isAlive()
                );

                if (!targets.isEmpty()) {
                    // 使用批处理添加效果
                    targets.forEach(target -> {
                        StatusEffectInstance existing = target.getStatusEffect(ModEffects.CYRO);
                        if (existing == null || existing.getDuration() <= 195) {
                            target.addStatusEffect(new StatusEffectInstance(
                                    ModEffects.CYRO,
                                    200,
                                    0,
                                    false,
                                    true,
                                    true
                            ));
                        }
                    });

                    entity.discard();
                }
            }
        });
    }

}
