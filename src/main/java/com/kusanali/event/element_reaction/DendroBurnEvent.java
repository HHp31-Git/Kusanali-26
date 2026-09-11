package com.kusanali.event.element_reaction;

import com.kusanali.define.event.ElementReaction;
import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

public class DendroBurnEvent extends ElementReaction {
    @Override protected int getCooldown() { return 46; }
    @Override protected List<StatusEffect> getParticipatingEffects() {
        return List.of(ModEffects.DENDRO, ModEffects.PYRO);
    }

    @Override
    protected void onReact(LivingEntity entity, ServerWorld world, Map<StatusEffect, Long> times) {
        entity.addStatusEffect(new StatusEffectInstance(ModEffects.BURNING, 60, 0, false, true, true));
    }
}
