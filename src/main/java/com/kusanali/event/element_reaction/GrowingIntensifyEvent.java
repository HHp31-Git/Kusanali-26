package com.kusanali.event.element_reaction;

import com.kusanali.define.event.ElementReaction;
import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class GrowingIntensifyEvent extends ElementReaction {
    @Override protected int getCooldown() { return 20; }
    @Override protected List<StatusEffect> getParticipatingEffects() { return List.of(ModEffects.INTENSIFY, ModEffects.DENDRO); }
    @Override protected void onReact(LivingEntity e, ServerWorld w, Map<StatusEffect, Long> times) {
        e.damage(ModDamageTypes.reaction_type_4(w), 2.0f);
        Vec3d c = e.getPos();
        double cx = c.x, cy = c.y + e.getHeight() * 0.5, cz = c.z;
        w.spawnParticles(ParticleTypes.ELECTRIC_SPARK, cx, cy, cz, 8, 0.3, 0.3, 0.3, 0.08);
        w.spawnParticles(ParticleTypes.HAPPY_VILLAGER, cx, cy, cz, 12, 0.4, 0.4, 0.4, 0.16);
    }
}
