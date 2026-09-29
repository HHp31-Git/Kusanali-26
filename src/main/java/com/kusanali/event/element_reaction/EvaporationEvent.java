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
    @Override protected List<StatusEffect> getParticipatingEffects() { return List.of(ModEffects.PYRO, ModEffects.HYDRO); }
    @Override protected void onReact(LivingEntity e, ServerWorld w, Map<StatusEffect, Long> times) {
        boolean keepHydro = e.hasStatusEffect(ModEffects.PYRO) && Objects.requireNonNull(e.getStatusEffect(ModEffects.PYRO)).getDuration() == -1;
        float dmg = keepHydro ? 2.0f : 3.0f;
        e.damage(ModDamageTypes.reaction_type_1(w), dmg);
        double cx = e.getX(), cy = e.getY() + e.getHeight() * 0.5, cz = e.getZ();
        w.spawnParticles(ParticleTypes.CLOUD, cx, cy, cz, 20, 0.3, 0.3, 0.3, 0.05);
        w.spawnParticles(ParticleTypes.LARGE_SMOKE, cx, cy, cz, 8, 0.2, 0.2, 0.2, 0.02);
    }
}
