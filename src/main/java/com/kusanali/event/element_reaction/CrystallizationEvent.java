package com.kusanali.event.element_reaction;

import com.kusanali.define.event.ElementReaction;
import com.kusanali.register.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

public class CrystallizationEvent extends ElementReaction {
    private static final List<StatusEffect> CRYSTALLIZABLE = List.of(ModEffects.PYRO, ModEffects.HYDRO, ModEffects.ELECTRO, ModEffects.CYRO);
    private static final Map<StatusEffect, Integer> SHIELD_LEVEL = Map.of(ModEffects.PYRO, 2, ModEffects.HYDRO, 1, ModEffects.ELECTRO, 3, ModEffects.CYRO, 4);

    @Override protected int getCooldown() { return 20; }
    @Override protected List<StatusEffect> getParticipatingEffects() { return List.of(ModEffects.GEO); }
    @Override protected void onReact(LivingEntity e, ServerWorld w, Map<StatusEffect, Long> times) {
        StatusEffect targetEl = CRYSTALLIZABLE.stream().filter(e::hasStatusEffect).findFirst().orElse(null);
        if (targetEl == null) return;
        int level = SHIELD_LEVEL.getOrDefault(targetEl, 1);
        e.addStatusEffect(new StatusEffectInstance(ModEffects.GEO, 300, level, false, true, true));
        double cx = e.getX(), cy = e.getY() + e.getHeight() * 0.5, cz = e.getZ();
        w.spawnParticles(ParticleTypes.END_ROD, cx, cy, cz, 5, 0.3, 0.3, 0.3, 0.02);
    }
}
