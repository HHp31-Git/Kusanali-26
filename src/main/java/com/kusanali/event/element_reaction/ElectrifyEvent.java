package com.kusanali.event.element_reaction;

import com.kusanali.define.event.ElementReaction;
import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

public class ElectrifyEvent extends ElementReaction {
    @Override protected int getCooldown() { return 60; }
    @Override protected List<StatusEffect> getParticipatingEffects() {
        return List.of(ModEffects.ELECTRO, ModEffects.HYDRO);
    }

    @Override
    public void tickEntity(LivingEntity entity, ServerWorld world, long now) {
        UUID uuid = entity.getUuid();
        track(uuid, ModEffects.ELECTRO, entity, now);
        track(uuid, ModEffects.HYDRO, entity, now);

        long last = lastTriggerTime.getOrDefault(uuid, 0L);
        if (now - last < getCooldown()) { updatePrev(uuid, entity); return; }

        if (entity.hasStatusEffect(ModEffects.ELECTRIFY)) {
            reacted.remove(uuid);
            updatePrev(uuid, entity);
            return;
        }

        if (!entity.hasStatusEffect(ModEffects.ELECTRO) || !entity.hasStatusEffect(ModEffects.HYDRO)) {
            reacted.remove(uuid);
            updatePrev(uuid, entity);
            return;
        }

        if (reacted.contains(uuid)) { updatePrev(uuid, entity); return; }

        Map<StatusEffect, Long> times = applyTime.get(uuid);
        if (times == null) { updatePrev(uuid, entity); return; }

        boolean electroInf = Objects.requireNonNull(entity.getStatusEffect(ModEffects.ELECTRO)).getDuration() == -1;
        boolean hydroInf = Objects.requireNonNull(entity.getStatusEffect(ModEffects.HYDRO)).getDuration() == -1;

        if (electroInf && hydroInf) {
            applyReaction(entity, world);
            lastTriggerTime.put(uuid, now);
            reacted.add(uuid);
        } else if (electroInf) {
            entity.removeStatusEffect(ModEffects.HYDRO);
            times.remove(ModEffects.HYDRO);
            prev.computeIfAbsent(uuid,k->new HashMap<>()).put(ModEffects.HYDRO,false);
            applyReaction(entity, world);
            lastTriggerTime.put(uuid, now);
            reacted.add(uuid);
        } else {
            entity.removeStatusEffect(ModEffects.ELECTRO);
            times.remove(ModEffects.ELECTRO);
            prev.computeIfAbsent(uuid,k->new HashMap<>()).put(ModEffects.ELECTRO,false);
            applyReaction(entity, world);
            lastTriggerTime.put(uuid, now);
            reacted.add(uuid);
        }

        updatePrev(uuid, entity);
    }

    private void applyReaction(LivingEntity e, ServerWorld w) {
        e.addStatusEffect(new StatusEffectInstance(ModEffects.ELECTRIFY, 200, 0, false, true, true));
        e.damage(ModDamageTypes.reaction_type_2(w), 1.0f);
        w.spawnParticles(ParticleTypes.ELECTRIC_SPARK, e.getX(), e.getY()+e.getHeight()*0.5, e.getZ(), 3, 0.1,0.1,0.1,0.05);
    }

    @Override protected void onReact(LivingEntity entity, ServerWorld world, Map<StatusEffect, Long> times) {}
}
