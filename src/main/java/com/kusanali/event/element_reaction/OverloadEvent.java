package com.kusanali.event.element_reaction;

import com.kusanali.define.event.ElementReaction;
import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class OverloadEvent extends ElementReaction {
    @Override protected int getCooldown() { return 20; }
    @Override protected List<StatusEffect> getParticipatingEffects() {
        return List.of(ModEffects.ELECTRO, ModEffects.PYRO);
    }

    @Override
    public void tickEntity(LivingEntity entity, ServerWorld world, long now) {
        UUID uuid = entity.getUuid();
        track(uuid, ModEffects.ELECTRO, entity, now);
        track(uuid, ModEffects.PYRO, entity, now);

        long last = lastTriggerTime.getOrDefault(uuid, 0L);
        if (now - last < getCooldown()) { updatePrev(uuid, entity); return; }

        if (!entity.hasStatusEffect(ModEffects.ELECTRO) || !entity.hasStatusEffect(ModEffects.PYRO)) {
            reacted.remove(uuid);
            updatePrev(uuid, entity);
            return;
        }

        if (reacted.contains(uuid)) { updatePrev(uuid, entity); return; }

        Map<StatusEffect, Long> times = applyTime.get(uuid);
        if (times == null) { updatePrev(uuid, entity); return; }

        boolean electroInf = Objects.requireNonNull(entity.getStatusEffect(ModEffects.ELECTRO)).getDuration() == -1;
        boolean pyroInf = Objects.requireNonNull(entity.getStatusEffect(ModEffects.PYRO)).getDuration() == -1;

        if (electroInf && pyroInf) {
            applyOverload(entity, world);
        } else if (electroInf) {
            entity.removeStatusEffect(ModEffects.PYRO);
            times.remove(ModEffects.PYRO);
            prev.computeIfAbsent(uuid,k->new HashMap<>()).put(ModEffects.PYRO,false);
            applyOverload(entity, world);
        } else if (pyroInf) {
            entity.removeStatusEffect(ModEffects.ELECTRO);
            times.remove(ModEffects.ELECTRO);
            prev.computeIfAbsent(uuid,k->new HashMap<>()).put(ModEffects.ELECTRO,false);
            applyOverload(entity, world);
        } else {
            long electroTime = times.getOrDefault(ModEffects.ELECTRO, 0L);
            long pyroTime = times.getOrDefault(ModEffects.PYRO, 0L);
            boolean pyroLater = (electroTime == pyroTime) || (electroTime < pyroTime);
            StatusEffect earlier = pyroLater ? ModEffects.PYRO : ModEffects.ELECTRO;
            entity.removeStatusEffect(earlier);
            times.remove(earlier);
            prev.computeIfAbsent(uuid,k->new HashMap<>()).put(earlier,false);
            applyOverload(entity, world);
        }

        lastTriggerTime.put(uuid, now);
        reacted.add(uuid);
        updatePrev(uuid, entity);
    }

    private void applyOverload(LivingEntity e, ServerWorld w) {
        e.damage(ModDamageTypes.reaction_type_3(w), 2.5f);
        Vec3d pos = e.getPos();
        w.createExplosion(null, pos.x, pos.y + 0.5, pos.z, 0.5f, false, net.minecraft.world.World.ExplosionSourceType.NONE);
        float yaw = e.getYaw();
        double rad = Math.toRadians(yaw);
        e.takeKnockback(0.4, -Math.sin(rad), Math.cos(rad));
    }

    @Override protected void onReact(LivingEntity entity, ServerWorld world, Map<StatusEffect, Long> times) {}
}
