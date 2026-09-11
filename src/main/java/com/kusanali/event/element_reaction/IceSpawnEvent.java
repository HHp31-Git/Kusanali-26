package com.kusanali.event.element_reaction;

import com.kusanali.define.event.ElementReaction;
import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

public class IceSpawnEvent extends ElementReaction {
    @Override protected int getCooldown() { return 70; }
    @Override protected List<StatusEffect> getParticipatingEffects() {
        return List.of(ModEffects.CYRO, ModEffects.HYDRO);
    }

    @Override
    public void tickEntity(LivingEntity entity, ServerWorld world, long now) {
        UUID uuid = entity.getUuid();
        track(uuid, ModEffects.CYRO, entity, now);
        track(uuid, ModEffects.HYDRO, entity, now);

        long last = lastTriggerTime.getOrDefault(uuid, 0L);
        if (now - last < getCooldown()) { updatePrev(uuid, entity); return; }

        if (entity.hasStatusEffect(ModEffects.FREEZING)) {
            reacted.remove(uuid);
            updatePrev(uuid, entity);
            return;
        }

        if (!entity.hasStatusEffect(ModEffects.CYRO) || !entity.hasStatusEffect(ModEffects.HYDRO)) {
            reacted.remove(uuid);
            updatePrev(uuid, entity);
            return;
        }

        if (reacted.contains(uuid)) { updatePrev(uuid, entity); return; }

        Map<StatusEffect, Long> times = applyTime.get(uuid);
        if (times == null) { updatePrev(uuid, entity); return; }

        boolean cyroInf = Objects.requireNonNull(entity.getStatusEffect(ModEffects.CYRO)).getDuration() == -1;
        boolean hydroInf = Objects.requireNonNull(entity.getStatusEffect(ModEffects.HYDRO)).getDuration() == -1;

        if (cyroInf && hydroInf) {
            applyFreeze(entity, world);
        } else if (cyroInf) {
            entity.removeStatusEffect(ModEffects.HYDRO);
            times.remove(ModEffects.HYDRO);
            prev.computeIfAbsent(uuid,k->new HashMap<>()).put(ModEffects.HYDRO,false);
            applyFreeze(entity, world);
        } else if (hydroInf) {
            entity.removeStatusEffect(ModEffects.CYRO);
            times.remove(ModEffects.CYRO);
            prev.computeIfAbsent(uuid,k->new HashMap<>()).put(ModEffects.CYRO,false);
            applyFreeze(entity, world);
        } else {
            long cyroTime = times.getOrDefault(ModEffects.CYRO, 0L);
            long hydroTime = times.getOrDefault(ModEffects.HYDRO, 0L);
            boolean cyroLater = (cyroTime == hydroTime) || (cyroTime > hydroTime);
            StatusEffect earlier = cyroLater ? ModEffects.HYDRO : ModEffects.CYRO;
            entity.removeStatusEffect(earlier);
            times.remove(earlier);
            prev.computeIfAbsent(uuid,k->new HashMap<>()).put(earlier,false);
            applyFreeze(entity, world);
        }

        lastTriggerTime.put(uuid, now);
        reacted.add(uuid);
        updatePrev(uuid, entity);
    }

    private void applyFreeze(LivingEntity e, ServerWorld w) {
        e.addStatusEffect(new StatusEffectInstance(ModEffects.FREEZING, 200, 0, false, true, true));
        e.damage(ModDamageTypes.reaction_type_3(w), 1.0f);
    }

    @Override protected void onReact(LivingEntity entity, ServerWorld world, Map<StatusEffect, Long> times) {}
}
