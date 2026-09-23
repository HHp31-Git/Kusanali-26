package com.kusanali.define.event;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.world.World;

public abstract class ElementAdhering {
    protected abstract StatusEffect getTargetEffect();
    protected abstract boolean checkCondition(LivingEntity entity, World world);

    protected int getEffectDuration() { return 200; }
    protected int getRefreshInterval() { return 50; }

    public void tryAdhere(LivingEntity entity, World world) {
        if (!checkCondition(entity, world)) return;

        StatusEffectInstance existing = entity.getStatusEffect(getTargetEffect());
        if (existing != null && existing.getDuration() > getRefreshInterval()) return;

        entity.addStatusEffect(new StatusEffectInstance(
                getTargetEffect(), getEffectDuration(), 0, false, true, true
        ));
    }
}
