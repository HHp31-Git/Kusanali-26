package com.kusanali.event.element_adhering;

import com.kusanali.define.event.ElementAdhering;
import com.kusanali.register.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.world.World;

import static com.kusanali.register.ModEffects.BASE_ELEMENTS_ARRAY;

public class SlimeRandomEvent extends ElementAdhering {
    private static final TrackedData<Boolean> ELEMENT_INITIALIZED =
            DataTracker.registerData(SlimeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Override
    protected StatusEffect getTargetEffect() {
        // 返回一个默认的元素效果，实际效果会在 checkCondition 中随机选择
        return ModEffects.ANEMO;
    }

    @Override
    protected boolean checkCondition(LivingEntity entity, World world) {
        if (!(entity instanceof SlimeEntity slime)) return false;
        if (slime.getDataTracker().get(ELEMENT_INITIALIZED)) return false;

        // 使用位掩码快速检查元素效果
        int effectMask = 0;
        for (int i = 0; i < BASE_ELEMENTS_ARRAY.length; i++) {
            if (slime.hasStatusEffect(BASE_ELEMENTS_ARRAY[i])) {
                effectMask |= (1 << i);
            }
        }

        // 如果没有元素效果，则添加
        if (effectMask == 0) {
            // 使用预计算的随机表
            int seed = entity.getUuid().hashCode();
            int index = (seed & 0x7FFFFFFF) % BASE_ELEMENTS_ARRAY.length;
            StatusEffect effect = BASE_ELEMENTS_ARRAY[index];

            slime.addStatusEffect(new StatusEffectInstance(
                    effect,
                    -1,
                    0,
                    false,
                    true,
                    true
            ));
        }
        slime.getDataTracker().set(ELEMENT_INITIALIZED, true);
        return true;
    }

    public static void register() {
        // 监听史莱姆实体加载，初始化 DataTracker
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof SlimeEntity slime) {
                slime.getDataTracker().startTracking(ELEMENT_INITIALIZED, false);
            }
        });
    }
}

