package com.kusanali.event.element_reaction;

import com.kusanali.define.event.ElementReaction;
import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

public class MeltEvent extends ElementReaction {
    @Override protected int getCooldown() { return 20; }
    @Override protected List<StatusEffect> getParticipatingEffects() { return List.of(ModEffects.PYRO, ModEffects.CYRO); }
    @Override protected void onReact(LivingEntity e, ServerWorld w, Map<StatusEffect, Long> times) {
        boolean keepPyro = e.hasStatusEffect(ModEffects.CYRO) && Objects.requireNonNull(e.getStatusEffect(ModEffects.CYRO)).getDuration() == -1;
        boolean keepCyro = e.hasStatusEffect(ModEffects.PYRO) && Objects.requireNonNull(e.getStatusEffect(ModEffects.PYRO)).getDuration() == -1;
        float dmg = keepCyro ? 1.5f : 2.0f;
        e.damage(ModDamageTypes.reaction_type_1(w), dmg);

        double cx=e.getX(), cy=e.getY()+e.getHeight()*0.5, cz=e.getZ();
        int flameCount = keepPyro ? 18 : 9;
        int iceCount = keepCyro ? 18 : 9;
        for (int i = 0; i < flameCount; i++) {
            double a = w.random.nextDouble()*Math.PI*2, r = w.random.nextDouble()+0.2;
            w.spawnParticles(ParticleTypes.FLAME, cx+Math.cos(a)*r, cy+(w.random.nextDouble()-0.5)*1.5, cz+Math.sin(a)*r, 1, Math.cos(a)*0.05,0.1,Math.sin(a)*0.05,0.02);
        }
        for (int i = 0; i < iceCount; i++) {
            double a = w.random.nextDouble()*Math.PI*2, r = w.random.nextDouble()*0.8+0.2;
            w.spawnParticles(ParticleTypes.ITEM_SNOWBALL, cx+Math.cos(a)*r, cy+(w.random.nextDouble()-0.5)*1.2, cz+Math.sin(a)*r, 1, Math.cos(a)*0.03,0.05,Math.sin(a)*0.03,0.04);
        }
        for (int i = 0; i < 3; i++) {
            double px = cx+(w.random.nextDouble()-0.5)*0.4, pz = cz+(w.random.nextDouble()-0.5)*0.4;
            w.spawnParticles(ParticleTypes.SMALL_FLAME, px, cy+0.2, pz, 1, 0, 0.15, 0, 0.01);
        }
        for (int i = 0; i < 5; i++) {
            double a = w.random.nextDouble()*Math.PI*2, s = 0.05+w.random.nextDouble()*0.1;
            w.spawnParticles(ParticleTypes.SNOWFLAKE, cx+Math.cos(a)*0.3, cy+(w.random.nextDouble()-0.5)*0.5, cz+Math.sin(a)*0.3, 1, Math.cos(a)*s, w.random.nextDouble()*0.1, Math.sin(a)*s, 0.01);
        }
    }
}
