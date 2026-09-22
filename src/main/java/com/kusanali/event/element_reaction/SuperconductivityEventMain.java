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
    @Override protected List<StatusEffect> getParticipatingEffects() { return List.of(ModEffects.ELECTRO, ModEffects.CYRO); }
    @Override protected void onReact(LivingEntity e, ServerWorld w, Map<StatusEffect, Long> times) {
        e.damage(ModDamageTypes.reaction_type_2(w), 2.0f);
        e.addStatusEffect(new StatusEffectInstance(ModEffects.SUPERCONDUCTIVITY, 220, 0, false, false, true));
        double x = e.getX(), y = e.getY() + e.getHeight() * 0.5, z = e.getZ();
        w.spawnParticles(ParticleTypes.SNOWFLAKE, x, y, z, 10, 1.0, 0.5, 1.0, 0.1);
        w.spawnParticles(ParticleTypes.SNOWFLAKE, x, y + 0.5, z, 5, 1.5, 0.3, 1.5, 0.02);
    }
}
