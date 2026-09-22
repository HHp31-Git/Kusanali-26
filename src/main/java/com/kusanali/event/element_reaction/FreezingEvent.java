package com.kusanali.event.element_reaction;

import com.kusanali.define.event.ElementReaction;
import com.kusanali.register.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

public class FreezingEvent extends ElementReaction {
    @Override protected int getCooldown() { return 20; }
    @Override protected List<StatusEffect> getParticipatingEffects() { return List.of(ModEffects.CYRO, ModEffects.HYDRO); }
    @Override protected void onReact(LivingEntity e, ServerWorld w, Map<StatusEffect, Long> times) {
        e.addStatusEffect(new StatusEffectInstance(ModEffects.FREEZING, 200, 0, false, true, true));
        double cx = e.getX(), cy = e.getY() + e.getHeight() * 0.5, cz = e.getZ();
        w.spawnParticles(ParticleTypes.SNOWFLAKE, cx, cy, cz, 10, 0.5, 0.5, 0.5, 0.05);
    }
}
