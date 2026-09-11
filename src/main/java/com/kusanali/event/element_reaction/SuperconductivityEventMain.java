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

public class SuperconductivityEventMain extends ElementReaction {
    @Override protected int getCooldown() { return 20; }
    @Override protected List<StatusEffect> getParticipatingEffects() {
        return List.of(ModEffects.ELECTRO, ModEffects.CYRO);
    }

    @Override
    public void tickEntity(LivingEntity entity, ServerWorld world, long now) {
        UUID uuid = entity.getUuid();
        track(uuid, ModEffects.ELECTRO, entity, now);
        track(uuid, ModEffects.CYRO, entity, now);

        long last = lastTriggerTime.getOrDefault(uuid, 0L);
        if (now - last < getCooldown()) { updatePrev(uuid, entity); return; }

        if (entity.hasStatusEffect(ModEffects.SUPERCONDUCTIVITY)) {
            reacted.remove(uuid);
            updatePrev(uuid, entity);
            return;
        }

        if (!entity.hasStatusEffect(ModEffects.ELECTRO) || !entity.hasStatusEffect(ModEffects.CYRO)) {
            reacted.remove(uuid);
            updatePrev(uuid, entity);
            return;
        }

        if (reacted.contains(uuid)) { updatePrev(uuid, entity); return; }

        Map<StatusEffect, Long> times = applyTime.get(uuid);
        if (times == null) { updatePrev(uuid, entity); return; }

        boolean electroInf = Objects.requireNonNull(entity.getStatusEffect(ModEffects.ELECTRO)).getDuration() == -1;
        boolean cyroInf = Objects.requireNonNull(entity.getStatusEffect(ModEffects.CYRO)).getDuration() == -1;

        if (electroInf && cyroInf) {
            applySuperconductivity(entity, world);
        } else if (electroInf) {
            entity.removeStatusEffect(ModEffects.CYRO);
            times.remove(ModEffects.CYRO);
            prev.computeIfAbsent(uuid,k->new HashMap<>()).put(ModEffects.CYRO,false);
            applySuperconductivity(entity, world);
        } else if (cyroInf) {
            entity.removeStatusEffect(ModEffects.ELECTRO);
            times.remove(ModEffects.ELECTRO);
            prev.computeIfAbsent(uuid,k->new HashMap<>()).put(ModEffects.ELECTRO,false);
            applySuperconductivity(entity, world);
        } else {
            long electroTime = times.getOrDefault(ModEffects.ELECTRO, 0L);
            long cyroTime = times.getOrDefault(ModEffects.CYRO, 0L);
            boolean electroLater = (electroTime == cyroTime) || (electroTime > cyroTime);
            StatusEffect earlier = electroLater ? ModEffects.CYRO : ModEffects.ELECTRO;
            entity.removeStatusEffect(earlier);
            times.remove(earlier);
            prev.computeIfAbsent(uuid,k->new HashMap<>()).put(earlier,false);
            applySuperconductivity(entity, world);
        }

        lastTriggerTime.put(uuid, now);
        reacted.add(uuid);
        updatePrev(uuid, entity);
    }

    private void applySuperconductivity(LivingEntity e, ServerWorld w) {
        e.damage(ModDamageTypes.reaction_type_3(w), 2.0f);
        e.addStatusEffect(new StatusEffectInstance(ModEffects.SUPERCONDUCTIVITY, 220, 0, false, false, true));

        double x = e.getX(), y = e.getY() + e.getHeight() * 0.5, z = e.getZ();
        w.spawnParticles(ParticleTypes.SNOWFLAKE, x, y, z, 10, 1.0, 0.5, 1.0, 0.1);
        w.spawnParticles(ParticleTypes.SNOWFLAKE, x, y + 0.5, z, 5, 1.5, 0.3, 1.5, 0.02);
    }

    @Override protected void onReact(LivingEntity entity, ServerWorld world, Map<StatusEffect, Long> times) {}
}
