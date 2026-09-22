package com.kusanali.event.element_reaction;

import com.kusanali.define.event.ElementReaction;
import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

public class IceBreakEvent extends ElementReaction {
    @Override protected int getCooldown() { return 20; }
    @Override protected List<StatusEffect> getParticipatingEffects() { return List.of(ModEffects.FREEZING, ModEffects.PYRO); }
    @Override protected void onReact(LivingEntity e, ServerWorld w, Map<StatusEffect, Long> times) {
        e.damage(ModDamageTypes.reaction_type_2(w), 1.0f);
        double cx = e.getX(), cy = e.getY() + e.getHeight() * 0.5, cz = e.getZ();
        w.spawnParticles(ParticleTypes.ITEM_SNOWBALL, cx, cy, cz, 10, 0.3, 0.3, 0.3, 0.05);
    }
}
