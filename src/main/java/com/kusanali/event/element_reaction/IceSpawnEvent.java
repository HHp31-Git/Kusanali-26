package com.kusanali.event.element_reaction;

import com.kusanali.define.event.ElementReaction;
import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

public class IceSpawnEvent extends ElementReaction {
    @Override protected int getCooldown() { return 20; }
    @Override protected List<StatusEffect> getParticipatingEffects() { return List.of(ModEffects.FREEZING, ModEffects.CYRO); }
    @Override protected void onReact(LivingEntity e, ServerWorld w, Map<StatusEffect, Long> times) {
        e.damage(ModDamageTypes.reaction_type_2(w), 1.5f);
        double cx = e.getX(), cy = e.getY() + e.getHeight() * 0.5, cz = e.getZ();
        w.spawnParticles(ParticleTypes.ITEM_SNOWBALL, cx, cy, cz, 15, 0.4, 0.4, 0.4, 0.1);
    }
}
