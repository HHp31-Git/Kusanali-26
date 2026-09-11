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
    protected final Map<UUID, Map<StatusEffect, Boolean>> prev = new WeakHashMap<>();
    protected final Set<UUID> reacted = Collections.newSetFromMap(new WeakHashMap<>());
    protected final Map<UUID, Long> lastTriggerTime = new WeakHashMap<>();
    protected final Set<UUID> reentering = Collections.newSetFromMap(new WeakHashMap<>());

    protected abstract int getCooldown();
    protected abstract List<StatusEffect> getParticipatingEffects();
    protected abstract void onReact(LivingEntity entity, ServerWorld world, Map<StatusEffect, Long> times);

    public void tickEntity(LivingEntity entity, ServerWorld world, long now) {
        UUID uuid = entity.getUuid();

        for (StatusEffect effect : getParticipatingEffects()) {
            track(uuid, effect, entity, now);
        }

        long lastTime = lastTriggerTime.getOrDefault(uuid, 0L);
        if (now - lastTime < getCooldown()) {
            updatePrev(uuid, entity);
            return;
        }

        boolean hasAll = true;
        for (StatusEffect effect : getParticipatingEffects()) {
            if (!entity.hasStatusEffect(effect)) {
                hasAll = false;
                break;
            }
        }
        if (!hasAll) {
            reacted.remove(uuid);
            updatePrev(uuid, entity);
            return;
        }

        if (reacted.contains(uuid)) {
            updatePrev(uuid, entity);
            return;
        }

        Map<StatusEffect, Long> times = applyTime.get(uuid);
        if (times == null) {
            updatePrev(uuid, entity);
            return;
        }

        handleEffectsAndReact(entity, world, uuid, times, now);
        updatePrev(uuid, entity);
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
            reacted.add(uuid);
            return;
        }

        // 移除最早获得的有限效果
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
            prev.computeIfAbsent(uuid, k -> new HashMap<>()).put(earliest, false);
        }

        onReact(entity, world, times);
        lastTriggerTime.put(uuid, now);
        reacted.add(uuid);
    }

    protected void updatePrev(UUID uuid, LivingEntity entity) {
        Map<StatusEffect, Boolean> map = prev.computeIfAbsent(uuid, k -> new HashMap<>());
        for (StatusEffect effect : getParticipatingEffects()) {
            map.put(effect, entity.getStatusEffect(effect) != null);
        }
    }

    protected void track(UUID uuid, StatusEffect effect, LivingEntity entity, long now) {
        Map<StatusEffect, Boolean> prevMap = prev.get(uuid);
        boolean had = prevMap != null && prevMap.getOrDefault(effect, false);
        boolean has = entity.getStatusEffect(effect) != null;
        if (has && !had) {
            applyTime.computeIfAbsent(uuid, k -> new HashMap<>()).put(effect, now);
        }
    }
}