package com.kusanali.event.element_reaction;

import com.kusanali.define.event.ElementReaction;
import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

public class OverloadEvent extends ElementReaction {
    @Override protected int getCooldown() { return 20; }
    @Override protected List<StatusEffect> getParticipatingEffects() { return List.of(ModEffects.ELECTRO, ModEffects.PYRO); }
    @Override protected void onReact(LivingEntity e, ServerWorld w, Map<StatusEffect, Long> times) {
        e.damage(ModDamageTypes.reaction_type_2(w), 2.0f);
        w.spawnParticles(ParticleTypes.EXPLOSION, e.getX(), e.getY()+e.getHeight()*0.5, e.getZ(), 1, 0.1,0.1,0.1,0.02);
    }
}
