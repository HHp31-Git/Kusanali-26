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
    @Override protected int getCooldown() { return 18; }
    @Override protected List<StatusEffect> getParticipatingEffects() {
        return List.of(ModEffects.ELECTRO, ModEffects.DENDRO);
    }

    @Override
    public void tickEntity(LivingEntity entity, ServerWorld world, long now) {
        UUID uuid = entity.getUuid();
        track(uuid, ModEffects.ELECTRO, entity, now);
        track(uuid, ModEffects.DENDRO, entity, now);

        long last = lastTriggerTime.getOrDefault(uuid, 0L);
        if (now - last < getCooldown()) { updatePrev(uuid, entity); return; }

        if (entity.hasStatusEffect(ModEffects.INTENSIFY)) {
            reacted.remove(uuid);
            updatePrev(uuid, entity);
            return;
        }

        if (!entity.hasStatusEffect(ModEffects.ELECTRO) || !entity.hasStatusEffect(ModEffects.DENDRO)) {
            reacted.remove(uuid);
            updatePrev(uuid, entity);
            return;
        }

        if (reacted.contains(uuid)) { updatePrev(uuid, entity); return; }

        Map<StatusEffect, Long> times = applyTime.get(uuid);
        if (times == null) { updatePrev(uuid, entity); return; }

        boolean electroInf = Objects.requireNonNull(entity.getStatusEffect(ModEffects.ELECTRO)).getDuration() == -1;
        boolean dendroInf = Objects.requireNonNull(entity.getStatusEffect(ModEffects.DENDRO)).getDuration() == -1;

        if (electroInf && dendroInf) {
            applyReaction(entity, world);
        } else if (electroInf) {
            entity.removeStatusEffect(ModEffects.DENDRO);
            times.remove(ModEffects.DENDRO);
            prev.computeIfAbsent(uuid,k->new HashMap<>()).put(ModEffects.DENDRO,false);
            applyReaction(entity, world);
        } else if (dendroInf) {
            entity.removeStatusEffect(ModEffects.ELECTRO);
            times.remove(ModEffects.ELECTRO);
            prev.computeIfAbsent(uuid,k->new HashMap<>()).put(ModEffects.ELECTRO,false);
            applyReaction(entity, world);
        } else {
            entity.removeStatusEffect(ModEffects.DENDRO);
            times.remove(ModEffects.DENDRO);
            prev.computeIfAbsent(uuid,k->new HashMap<>()).put(ModEffects.DENDRO,false);
            entity.removeStatusEffect(ModEffects.ELECTRO);
            times.remove(ModEffects.ELECTRO);
            prev.computeIfAbsent(uuid,k->new HashMap<>()).put(ModEffects.ELECTRO,false);
            applyReaction(entity, world);
        }

        updatePrev(uuid, entity);
    }

    private void applyReaction(LivingEntity e, ServerWorld w) {
        e.addStatusEffect(new StatusEffectInstance(ModEffects.INTENSIFY, 300, 0, false, true, true));
        Vec3d c = e.getPos();
        w.spawnParticles(ParticleTypes.ELECTRIC_SPARK, c.x, c.y+e.getHeight()*0.5, c.z, 8, 0.3,0.3,0.3,0.08);
        w.spawnParticles(ParticleTypes.HAPPY_VILLAGER, c.x, c.y+e.getHeight()*0.5, c.z, 6, 0.2,0.2,0.2,0.06);
        w.spawnParticles(ParticleTypes.CRIT, c.x, c.y+e.getHeight()*0.5, c.z, 4, 0.1,0.1,0.1,0.1);
        lastTriggerTime.put(e.getUuid(), w.getTime());
    }

    @Override protected void onReact(LivingEntity entity, ServerWorld world, Map<StatusEffect, Long> times) {}
}
