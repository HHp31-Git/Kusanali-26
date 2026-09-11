package com.kusanali.event.element_reaction;

import com.kusanali.define.event.ElementReaction;
import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class CrystallizationEvent extends ElementReaction {
    private static final List<StatusEffect> CRYSTALLIZABLE = List.of(ModEffects.PYRO, ModEffects.HYDRO, ModEffects.ELECTRO, ModEffects.CYRO);
    private static final Map<StatusEffect, Integer> SHIELD_LEVEL = Map.of(ModEffects.PYRO, 2, ModEffects.HYDRO, 1, ModEffects.ELECTRO, 3, ModEffects.CYRO, 4);

    @Override protected int getCooldown() { return 18; }
    @Override protected List<StatusEffect> getParticipatingEffects() {
        return List.of(ModEffects.GEO);
    }

    @Override
    public void tickEntity(LivingEntity entity, ServerWorld world, long now) {
        UUID uuid = entity.getUuid();
        track(uuid, ModEffects.GEO, entity, now);
        for (StatusEffect e : CRYSTALLIZABLE) track(uuid, e, entity, now);

        long last = lastTriggerTime.getOrDefault(uuid, 0L);
        if (now - last < getCooldown()) { updatePrev(uuid, entity); return; }

        if (!entity.hasStatusEffect(ModEffects.GEO)) { reacted.remove(uuid); updatePrev(uuid, entity); return; }

        StatusEffect element = null;
        for (StatusEffect e : CRYSTALLIZABLE) {
            if (entity.hasStatusEffect(e)) { element = e; break; }
        }
        if (element == null) { reacted.remove(uuid); updatePrev(uuid, entity); return; }
        if (reacted.contains(uuid)) { updatePrev(uuid, entity); return; }

        Map<StatusEffect, Long> times = applyTime.get(uuid);
        if (times == null) { updatePrev(uuid, entity); return; }

        boolean geoInf = Objects.requireNonNull(entity.getStatusEffect(ModEffects.GEO)).getDuration() == -1;
        boolean elemInf = Objects.requireNonNull(entity.getStatusEffect(element)).getDuration() == -1;

        if (!geoInf) { entity.removeStatusEffect(ModEffects.GEO); times.remove(ModEffects.GEO); prev.computeIfAbsent(uuid,k->new HashMap<>()).put(ModEffects.GEO,false); }
        if (!elemInf) { entity.removeStatusEffect(element); times.remove(element);
            prev.computeIfAbsent(uuid,k->new HashMap<>()).put(element,false); }

        applyShield(entity, element);
        spawnParticles(world, entity);
        entity.damage(ModDamageTypes.reaction_type_3(world), 1.0f);
        lastTriggerTime.put(uuid, now);
        reacted.add(uuid);
        updatePrev(uuid, entity);
    }

    private void applyShield(LivingEntity e, StatusEffect el) {
        e.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 300, SHIELD_LEVEL.getOrDefault(el, 0), false, false, true));
    }

    private void spawnParticles(ServerWorld w, LivingEntity e) {
        Vec3d c = e.getPos(); double cx=c.x, cy=c.y+e.getHeight()*0.5, cz=c.z;
        for (int i = 0; i < 6; i++) {
            double a = (Math.PI*2/6)*i;
            w.spawnParticles(ParticleTypes.CRIT, cx+Math.cos(a)*1.2, cy+0.3, cz+Math.sin(a)*1.2, 3, 0.1,0.1,0.1,0.05);
        }
        for (int ring = 0; ring < 2; ring++) {
            double rr = 0.8+ring*0.6;
            for (int i = 0; i < 8; i++) {
                double a = (Math.PI*2/8)*i;
                w.spawnParticles(ParticleTypes.CLOUD, cx+Math.cos(a)*rr, cy-0.3, cz+Math.sin(a)*rr, 1, 0,0.02,0,0.02);
            }
        }
    }

    @Override protected void onReact(LivingEntity entity, ServerWorld world, Map<StatusEffect, Long> times) {}
}
