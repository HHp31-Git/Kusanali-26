package com.kusanali.event.element_reaction;

import com.kusanali.define.event.ElementReaction;
import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

public class DiffusionEvent extends ElementReaction {
    private static final List<StatusEffect> SWIRLABLE = List.of(ModEffects.CYRO, ModEffects.PYRO, ModEffects.HYDRO);
    private static final Map<StatusEffect, ParticleEffect> PARTICLES = Map.of(
            ModEffects.CYRO, ParticleTypes.ITEM_SNOWBALL, ModEffects.PYRO, ParticleTypes.FLAME, ModEffects.HYDRO, ParticleTypes.FALLING_WATER);
    private static final int EXTEND = 200;

    @Override protected int getCooldown() { return 14; }
    @Override protected List<StatusEffect> getParticipatingEffects() { return List.of(ModEffects.ANEMO); }
    @Override protected void onReact(LivingEntity entity, ServerWorld world, Map<StatusEffect, Long> times) {
        StatusEffect spreadEl = SWIRLABLE.stream().filter(entity::hasStatusEffect).findFirst().orElse(null);
        if (spreadEl == null) return;

        var instance = entity.getStatusEffect(spreadEl);
        if (instance != null && instance.getDuration() != -1) {
            entity.removeStatusEffect(spreadEl);
        }

        world.getEntitiesByClass(LivingEntity.class, entity.getBoundingBox().expand(3.5), e -> e != entity).forEach(target -> {
            if (target.hasStatusEffect(spreadEl)) {
                var tInst = target.getStatusEffect(spreadEl);
                if(tInst != null && tInst.getDuration() != -1) {
                    target.addStatusEffect(new StatusEffectInstance(spreadEl, tInst.getDuration() + EXTEND, tInst.getAmplifier(), false, true, true));
                }
            } else {
                target.addStatusEffect(new StatusEffectInstance(spreadEl, EXTEND, 0, false, true, true));
            }
        });
        entity.damage(ModDamageTypes.reaction_type_2(world), 1.0f);

        double cx = entity.getX(), cy = entity.getY() + entity.getHeight() * 0.5, cz = entity.getZ();
        world.spawnParticles(PARTICLES.get(spreadEl), cx, cy, cz, 10, 0.5, 0.5, 0.5, 0.05);
    }
}
