package com.kusanali.event.element_adhering;

import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.SlimeEntity;

public class SlimeRandomEvent {

    private static final StatusEffect[] ELEMENTS = {
            ModEffects.GEO,
            ModEffects.PYRO,
            ModEffects.CYRO,
            ModEffects.ELECTRO,
            ModEffects.ANEMO,
            ModEffects.HYDRO,
            ModEffects.DENDRO
    };

    /** DataTracker 标记，记录史莱姆是否已初始化元素 */
    private static final TrackedData<Boolean> ELEMENT_INITIALIZED =
            DataTracker.registerData(SlimeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public static void register() {
        // 监听史莱姆实体加载，初始化 DataTracker
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof SlimeEntity slime) {
                slime.getDataTracker().startTracking(ELEMENT_INITIALIZED, false);
            }
        });

        ServerTickEvents.END_WORLD_TICK.register(world -> {
            for (Entity entity : world.iterateEntities()) {
                if (!(entity instanceof SlimeEntity slime)) continue;

                // 已初始化则跳过
                if (slime.getDataTracker().get(ELEMENT_INITIALIZED)) continue;

                // 使用位掩码快速检查元素效果
                int effectMask = 0;
                for (int i = 0; i < ELEMENTS.length; i++) {
                    if (slime.hasStatusEffect(ELEMENTS[i])) {
                        effectMask |= (1 << i);
                    }
                }

                // 如果没有元素效果，则添加
                if (effectMask == 0) {
                    // 使用预计算的随机表
                    int seed = entity.getUuid().hashCode();
                    int index = (seed & 0x7FFFFFFF) % ELEMENTS.length;
                    StatusEffect effect = ELEMENTS[index];

                    slime.addStatusEffect(new StatusEffectInstance(
                            effect,
                            -1, // 无限时长
                            0,
                            false,
                            true,
                            true
                    ));
                }

                // 标记已初始化，不再重复处理
                slime.getDataTracker().set(ELEMENT_INITIALIZED, true);
            }
        });
    }
}
