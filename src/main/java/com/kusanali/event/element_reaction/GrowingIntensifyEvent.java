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
    @Override protected int getCooldown() { return 18; }
    @Override protected List<StatusEffect> getParticipatingEffects() {
        return List.of(ModEffects.INTENSIFY, ModEffects.DENDRO);
    }

    @Override
    public void tickEntity(LivingEntity entity, ServerWorld world, long now) {
        UUID uuid = entity.getUuid();
        track(uuid, ModEffects.INTENSIFY, entity, now);
        track(uuid, ModEffects.DENDRO, entity, now);

        long last = lastTriggerTime.getOrDefault(uuid, 0L);
        if (now - last < getCooldown()) { updatePrev(uuid, entity); return; }

        if (!entity.hasStatusEffect(ModEffects.INTENSIFY) || !entity.hasStatusEffect(ModEffects.DENDRO)) {
            reacted.remove(uuid);
            updatePrev(uuid, entity);
            return;
        }

        if (reacted.contains(uuid)) { updatePrev(uuid, entity); return; }

        Map<StatusEffect, Long> times = applyTime.get(uuid);
        if (times == null) { updatePrev(uuid, entity); return; }

        boolean intensifyInf = Objects.requireNonNull(entity.getStatusEffect(ModEffects.INTENSIFY)).getDuration() == -1;
        boolean dendroInf = Objects.requireNonNull(entity.getStatusEffect(ModEffects.DENDRO)).getDuration() == -1;

        if (intensifyInf && dendroInf) {
            applyReaction(entity, world);
        } else if (intensifyInf) {
            entity.removeStatusEffect(ModEffects.DENDRO);
            times.remove(ModEffects.DENDRO);
            prev.computeIfAbsent(uuid,k->new HashMap<>()).put(ModEffects.DENDRO,false);
            applyReaction(entity, world);
        } else if (dendroInf) {
            entity.removeStatusEffect(ModEffects.INTENSIFY);
            times.remove(ModEffects.INTENSIFY);
            prev.computeIfAbsent(uuid,k->new HashMap<>()).put(ModEffects.INTENSIFY,false);
            applyReaction(entity, world);
        } else {
            entity.removeStatusEffect(ModEffects.INTENSIFY);
            times.remove(ModEffects.INTENSIFY);
            prev.computeIfAbsent(uuid,k->new HashMap<>()).put(ModEffects.INTENSIFY,false);
            entity.removeStatusEffect(ModEffects.DENDRO);
            times.remove(ModEffects.DENDRO);
            prev.computeIfAbsent(uuid,k->new HashMap<>()).put(ModEffects.DENDRO,false);
            applyReaction(entity, world);
        }

        updatePrev(uuid, entity);
    }

    private void applyReaction(LivingEntity e, ServerWorld w) {
        e.damage(ModDamageTypes.reaction_type_4(w), 2.0f);
        Vec3d c = e.getPos();
        double cx = c.x, cy = c.y + e.getHeight() * 0.5, cz = c.z;
        w.spawnParticles(ParticleTypes.ELECTRIC_SPARK, cx, cy, cz, 8, 0.3, 0.3, 0.3, 0.08);
        w.spawnParticles(ParticleTypes.HAPPY_VILLAGER, cx, cy, cz, 12, 0.4, 0.4, 0.4, 0.16);
        lastTriggerTime.put(e.getUuid(), w.getTime());
    }

    @Override protected void onReact(LivingEntity entity, ServerWorld world, Map<StatusEffect, Long> times) {}
}
