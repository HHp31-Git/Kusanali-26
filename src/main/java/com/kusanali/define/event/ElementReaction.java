package com.kusanali.define.event;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

/**
 * 元素反应基类，封装通用的跟踪、冷却、防重入逻辑
 */
public abstract class ElementReaction {

    protected final Map<UUID, Map<StatusEffect, Long>> applyTime = new WeakHashMap<>();
    protected final Map<UUID, Long> lastTriggerTime = new WeakHashMap<>();

    protected abstract int getCooldown();
    protected abstract List<StatusEffect> getParticipatingEffects();
    protected abstract void onReact(LivingEntity entity, ServerWorld world, Map<StatusEffect, Long> times);

    public void checkAndReact(LivingEntity entity, ServerWorld world, StatusEffect changedEffect) {
        if (!getParticipatingEffects().contains(changedEffect)) return;

        for (StatusEffect required : getParticipatingEffects()) {
            if (!entity.hasStatusEffect(required)) return;
        }

        long now = world.getTime();
        long last = lastTriggerTime.getOrDefault(entity.getUuid(), 0L);
        if (now - last < getCooldown()) return;

        Map<StatusEffect, Long> times = applyTime.computeIfAbsent(entity.getUuid(), k -> new HashMap<>());
        times.put(changedEffect, now);
        handleEffectsAndReact(entity, world, entity.getUuid(), times, now);
        lastTriggerTime.put(entity.getUuid(), now);
    }

    private void handleEffectsAndReact(LivingEntity entity, ServerWorld world, UUID uuid,
                                       Map<StatusEffect, Long> times, long now) {
        List<StatusEffect> effects = getParticipatingEffects();
        boolean allInfinite = true;

        for (StatusEffect effect : effects) {
            var instance = entity.getStatusEffect(effect);
            if (instance == null) continue;
            if (instance.getDuration() != -1) {
                allInfinite = false;
                break;
            }
        }

        if (allInfinite) {
            onReact(entity, world, times);
            lastTriggerTime.put(uuid, now);
            return;
        }

        StatusEffect earliest = null;
        long earliestTime = Long.MAX_VALUE;
        for (StatusEffect effect : effects) {
            var instance = entity.getStatusEffect(effect);
            if (instance == null || instance.getDuration() == -1) continue;
            Long time = times.get(effect);
            if (time != null && time < earliestTime) {
                earliestTime = time;
                earliest = effect;
            }
        }

        if (earliest != null) {
            entity.removeStatusEffect(earliest);
            times.remove(earliest);
        }

        onReact(entity, world, times);
        lastTriggerTime.put(uuid, now);
    }
}