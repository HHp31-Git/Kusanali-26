package com.kusanali.event.element_reaction;

import com.kusanali.define.event.ElementReaction;
import com.kusanali.register.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

public class CrystallizationEvent extends ElementReaction {
    private static final List<StatusEffect> CRYSTALLIZABLE = List.of(ModEffects.PYRO, ModEffects.HYDRO, ModEffects.ELECTRO, ModEffects.CYRO);

    @Override
    protected int getCooldown() { return 20; }
    private static final float SHIELD_AMOUNT = 4.0f;
    @Override
    protected List<StatusEffect> getParticipatingEffects() { return List.of(ModEffects.GEO); }
    @Override
    protected void onReact(LivingEntity e, ServerWorld w, Map<StatusEffect, Long> times) {
        if (e == null || !e.isAlive() || e.isRemoved()) return;
        StatusEffect targetEl = CRYSTALLIZABLE.stream().filter(e::hasStatusEffect).findFirst().orElse(null);
        if (targetEl == null) return;
        if (e.hasStatusEffect(ModEffects.GEO)) return;
        e.setAbsorptionAmount(SHIELD_AMOUNT);
        e.removeStatusEffect(targetEl);
        double cx = e.getX(), cy = e.getY() + e.getHeight() * 0.5, cz = e.getZ();
        w.spawnParticles(ParticleTypes.END_ROD, cx, cy, cz, 5, 0.3, 0.3, 0.3, 0.02);
    }
}
