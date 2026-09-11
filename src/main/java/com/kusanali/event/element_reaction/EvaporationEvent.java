package com.kusanali.event.element_reaction;

import com.kusanali.define.event.ElementReaction;
import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

public class EvaporationEvent extends ElementReaction {
    @Override protected int getCooldown() { return 20; }
    @Override protected List<StatusEffect> getParticipatingEffects() {
        return List.of(ModEffects.PYRO, ModEffects.HYDRO);
    }

    @Override
    public void tickEntity(LivingEntity entity, ServerWorld world, long now) {
        UUID uuid = entity.getUuid();
        track(uuid, ModEffects.PYRO, entity, now);
        track(uuid, ModEffects.HYDRO, entity, now);

        long last = lastTriggerTime.getOrDefault(uuid, 0L);
        if (now - last < getCooldown()) { updatePrev(uuid, entity); return; }

        if (!entity.hasStatusEffect(ModEffects.PYRO) || !entity.hasStatusEffect(ModEffects.HYDRO)) {
            reacted.remove(uuid);
            updatePrev(uuid, entity);
            return;
        }

        if (reacted.contains(uuid)) { updatePrev(uuid, entity); return; }

        Map<StatusEffect, Long> times = applyTime.get(uuid);
        if (times == null) { updatePrev(uuid, entity); return; }

        boolean pyroInf = Objects.requireNonNull(entity.getStatusEffect(ModEffects.PYRO)).getDuration() == -1;
        boolean hydroInf = Objects.requireNonNull(entity.getStatusEffect(ModEffects.HYDRO)).getDuration() == -1;

        if (pyroInf && hydroInf) {
            applyMeltDamage(entity, world, 2.0f);
        } else if (pyroInf) {
            entity.removeStatusEffect(ModEffects.HYDRO);
            times.remove(ModEffects.HYDRO);
            prev.computeIfAbsent(uuid,k->new HashMap<>()).put(ModEffects.HYDRO,false);
            applyMeltDamage(entity, world, 2.0f);
        } else if (hydroInf) {
            entity.removeStatusEffect(ModEffects.PYRO);
            times.remove(ModEffects.PYRO);
            prev.computeIfAbsent(uuid,k->new HashMap<>()).put(ModEffects.PYRO,false);
            applyMeltDamage(entity, world, 3.0f);
        } else {
            long pyroTime = times.getOrDefault(ModEffects.PYRO, 0L);
            long hydroTime = times.getOrDefault(ModEffects.HYDRO, 0L);
            boolean pyroLater = (pyroTime == hydroTime) || (pyroTime > hydroTime);
            StatusEffect earlier = pyroLater ? ModEffects.HYDRO : ModEffects.PYRO;
            entity.removeStatusEffect(earlier);
            times.remove(earlier);
            prev.computeIfAbsent(uuid,k->new HashMap<>()).put(earlier,false);
            applyMeltDamage(entity, world, pyroLater ? 2.0f : 3.0f);
        }

        lastTriggerTime.put(uuid, now);
        reacted.add(uuid);
        updatePrev(uuid, entity);
    }

    private void applyMeltDamage(LivingEntity e, ServerWorld w, float dmg) {
        if (reentering.contains(e.getUuid())) return;
        reentering.add(e.getUuid());
        try {
            e.damage(ModDamageTypes.reaction_type_2(w), dmg);
            spawnParticles(w, e);
        } finally {
            w.getServer().execute(() -> reentering.remove(e.getUuid()));
        }
    }

    private void spawnParticles(ServerWorld w, LivingEntity e) {
        double cx=e.getX(), cy=e.getY()+e.getHeight()*0.5, cz=e.getZ();
        for (int i = 0; i < 10; i++) {
            w.spawnParticles(ParticleTypes.POOF, cx+(w.random.nextDouble()-0.5)*1.2, cy+(w.random.nextDouble()-0.5)*0.8, cz+(w.random.nextDouble()-0.5)*1.2, 1, 0,0.1,0,0.04);
        }
    }

    @Override protected void onReact(LivingEntity entity, ServerWorld world, Map<StatusEffect, Long> times) {}
}
