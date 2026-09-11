package com.kusanali.event.element_reaction;

import com.kusanali.define.event.ElementReaction;
import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

import java.util.*;

public class DiffusionEvent extends ElementReaction {
    private static final List<StatusEffect> SWIRLABLE = List.of(ModEffects.CYRO, ModEffects.PYRO, ModEffects.HYDRO);
    private static final Map<StatusEffect, ParticleEffect> PARTICLES = Map.of(
            ModEffects.CYRO, ParticleTypes.ITEM_SNOWBALL, ModEffects.PYRO, ParticleTypes.FLAME, ModEffects.HYDRO, ParticleTypes.FALLING_WATER);
    private static final int EXTEND = 200;

    @Override protected int getCooldown() { return 14; }
    @Override protected List<StatusEffect> getParticipatingEffects() { return List.of(ModEffects.ANEMO); }

    @Override
    public void tickEntity(LivingEntity entity, ServerWorld world, long now) {
        UUID uuid = entity.getUuid();
        track(uuid, ModEffects.ANEMO, entity, now);
        for (StatusEffect e : SWIRLABLE) track(uuid, e, entity, now);

        long last = lastTriggerTime.getOrDefault(uuid, 0L);
        if (now - last < getCooldown()) { updatePrev(uuid, entity); return; }
        if (!entity.hasStatusEffect(ModEffects.ANEMO)) { reacted.remove(uuid); updatePrev(uuid, entity); return; }

        StatusEffect spreadEl = null;
        for (StatusEffect e : SWIRLABLE) { if (entity.hasStatusEffect(e)) { spreadEl = e; break; } }
        if (spreadEl == null) { reacted.remove(uuid); updatePrev(uuid, entity); return; }
        if (reacted.contains(uuid)) { updatePrev(uuid, entity); return; }

        Map<StatusEffect, Long> times = applyTime.get(uuid);
        if (times == null) { updatePrev(uuid, entity); return; }

        boolean anemoInf = Objects.requireNonNull(entity.getStatusEffect(ModEffects.ANEMO)).getDuration() == -1;
        boolean spreadInf = Objects.requireNonNull(entity.getStatusEffect(spreadEl)).getDuration() == -1;

        if (!anemoInf) { entity.removeStatusEffect(ModEffects.ANEMO); times.remove(ModEffects.ANEMO); prev.computeIfAbsent(uuid,k->new HashMap<>()).put(ModEffects.ANEMO,false); }
        if (!spreadInf) {
            entity.removeStatusEffect(spreadEl);
            entity.addStatusEffect(new StatusEffectInstance(spreadEl, EXTEND, 0, false, true, true));
            times.put(spreadEl, now);
            prev.computeIfAbsent(uuid,k->new HashMap<>()).put(spreadEl,true);
        }

        if (!reentering.contains(uuid)) {
            reentering.add(uuid);
            try { entity.damage(ModDamageTypes.reaction_type_2(world), 1.0f); }
            finally { world.getServer().execute(() -> reentering.remove(uuid)); }
        }

        spawnSwirlParticles(world, entity, spreadEl);
        Box area = new Box(entity.getX()-1.5, entity.getY()-1.5, entity.getZ()-1.5, entity.getX()+1.5, entity.getY()+1.5, entity.getZ()+1.5);
        for (var nearby : world.getEntitiesByClass(LivingEntity.class, area, e -> e != entity && e.isAlive())) {
            nearby.addStatusEffect(new StatusEffectInstance(spreadEl, EXTEND, 0, false, true, true));
            spawnTargetParticles(world, nearby, spreadEl);
        }

        lastTriggerTime.put(uuid, now);
        reacted.add(uuid);
        updatePrev(uuid, entity);
    }

    private void spawnSwirlParticles(ServerWorld w, LivingEntity e, StatusEffect el) {
        double cx=e.getX(), cy=e.getY()+e.getHeight()*0.5, cz=e.getZ();
        ParticleEffect p = PARTICLES.getOrDefault(el, ParticleTypes.HAPPY_VILLAGER);
        for (int i = 0; i < 20; i++) {
            double a = w.random.nextDouble()*Math.PI*2, r = w.random.nextDouble()*0.8+0.2;
            w.spawnParticles(p, cx+Math.cos(a)*r, cy+(w.random.nextDouble()-0.5)*1.5, cz+Math.sin(a)*r, 1, 0,0,0,0.05);
        }
        for (int ring = 0; ring < 3; ring++) {
            double rr = 0.5+ring*0.4;
            for (int i = 0; i < 8; i++) {
                double a = (Math.PI*2/8)*i + ring*0.5;
                w.spawnParticles(ParticleTypes.CLOUD, cx+Math.cos(a)*rr, cy, cz+Math.sin(a)*rr, 1, 0,0,0,0.03);
            }
        }
    }

    private void spawnTargetParticles(ServerWorld w, LivingEntity t, StatusEffect el) {
        double tx=t.getX(), ty=t.getY()+t.getHeight()*0.5, tz=t.getZ();
        ParticleEffect p = PARTICLES.getOrDefault(el, ParticleTypes.HAPPY_VILLAGER);
        for (int i = 0; i < 5; i++) {
            double a = w.random.nextDouble()*Math.PI*2;
            w.spawnParticles(p, tx+Math.cos(a)*0.3, ty, tz+Math.sin(a)*0.3, 1, 0,0,0,0.02);
        }
        w.spawnParticles(ParticleTypes.END_ROD, tx, ty, tz, 3, 0.2,0.2,0.2,0.01);
    }

    @Override protected void onReact(LivingEntity entity, ServerWorld world, Map<StatusEffect, Long> times) {}
}
