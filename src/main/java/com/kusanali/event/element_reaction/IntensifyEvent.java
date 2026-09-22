package com.kusanali.event.element_reaction;

import com.kusanali.define.event.ElementReaction;
import com.kusanali.register.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class IntensifyEvent extends ElementReaction {
    @Override protected int getCooldown() { return 20; }
    @Override protected List<StatusEffect> getParticipatingEffects() { return List.of(ModEffects.ELECTRO, ModEffects.DENDRO); }
    @Override protected void onReact(LivingEntity e, ServerWorld w, Map<StatusEffect, Long> times) {
        e.addStatusEffect(new StatusEffectInstance(ModEffects.INTENSIFY, 300, 0, false, true, true));
        Vec3d c = e.getPos();
        w.spawnParticles(ParticleTypes.ELECTRIC_SPARK, c.x, c.y+e.getHeight()*0.5, c.z, 8, 0.3,0.3,0.3,0.08);
        w.spawnParticles(ParticleTypes.HAPPY_VILLAGER, c.x, c.y+e.getHeight()*0.5, c.z, 6, 0.2,0.2,0.2,0.06);
        w.spawnParticles(ParticleTypes.CRIT, c.x, c.y+e.getHeight()*0.5, c.z, 4, 0.1,0.1,0.1,0.1);
    }
}
