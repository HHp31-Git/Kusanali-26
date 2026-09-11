package com.kusanali.event.element_reaction;

import com.kusanali.define.event.ElementReaction;
import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.*;

public class MeltEvent extends ElementReaction {
    @Override protected int getCooldown() { return 20; }
    @Override protected List<StatusEffect> getParticipatingEffects() {
        return List.of(ModEffects.PYRO, ModEffects.CYRO);
    }

    @Override
    public void tickEntity(LivingEntity entity, ServerWorld world, long now) {
        UUID uuid = entity.getUuid();
        track(uuid, ModEffects.PYRO, entity, now);
        track(uuid, ModEffects.CYRO, entity, now);

        long last = lastTriggerTime.getOrDefault(uuid, 0L);
        if (now - last < getCooldown()) { updatePrev(uuid, entity); return; }

        if (!entity.hasStatusEffect(ModEffects.PYRO) || !entity.hasStatusEffect(ModEffects.CYRO)) {
            reacted.remove(uuid);
            updatePrev(uuid, entity);
            return;
        }

        if (reacted.contains(uuid)) { updatePrev(uuid, entity); return; }

        Map<StatusEffect, Long> times = applyTime.get(uuid);
        if (times == null) { updatePrev(uuid, entity); return; }

        boolean pyroInf = Objects.requireNonNull(entity.getStatusEffect(ModEffects.PYRO)).getDuration() == -1;
        boolean cyroInf = Objects.requireNonNull(entity.getStatusEffect(ModEffects.CYRO)).getDuration() == -1;

        float damage;
        boolean keepPyro, keepCyro;

        if (pyroInf && cyroInf) {
            damage = 2.0f; keepPyro = true; keepCyro = true;
        } else if (pyroInf) {
            entity.removeStatusEffect(ModEffects.CYRO);
            times.remove(ModEffects.CYRO);
            prev.computeIfAbsent(uuid,k->new HashMap<>()).put(ModEffects.CYRO,false);
            damage = 2.0f; keepPyro = true; keepCyro = false;
        } else if (cyroInf) {
            entity.removeStatusEffect(ModEffects.PYRO);
            times.remove(ModEffects.PYRO);
            prev.computeIfAbsent(uuid,k->new HashMap<>()).put(ModEffects.PYRO,false);
            damage = 3.0f; keepPyro = false; keepCyro = true;
        } else {
            long pyroTime = times.getOrDefault(ModEffects.PYRO, 0L);
            long cyroTime = times.getOrDefault(ModEffects.CYRO, 0L);
            boolean keepCyroFlag = (pyroTime == cyroTime) || (pyroTime > cyroTime);
            StatusEffect earlier = keepCyroFlag ? ModEffects.CYRO : ModEffects.PYRO;
            entity.removeStatusEffect(earlier);
            times.remove(earlier);
            prev.computeIfAbsent(uuid,k->new HashMap<>()).put(earlier,false);
            damage = keepCyroFlag ? 2.0f : 3.0f;
            keepPyro = !keepCyroFlag; keepCyro = keepCyroFlag;
        }

        applyMelt(world, entity, damage, keepPyro, keepCyro);
        lastTriggerTime.put(uuid, now);
        reacted.add(uuid);
        updatePrev(uuid, entity);
    }

    private void applyMelt(ServerWorld w, LivingEntity e, float dmg, boolean keepPyro, boolean keepCyro) {
        if (!reentering.contains(e.getUuid())) {
            reentering.add(e.getUuid());
            try { e.damage(ModDamageTypes.reaction_type_1(w), dmg); }
            finally { w.getServer().execute(() -> reentering.remove(e.getUuid())); }
        }
        spawnParticles(w, e, keepPyro, keepCyro);
        playSound(w, e, dmg);
    }

    private void spawnParticles(ServerWorld w, LivingEntity e, boolean keepPyro, boolean keepCyro) {
        double cx=e.getX(), cy=e.getY()+e.getHeight()*0.5, cz=e.getZ();
        int flameCount = keepPyro ? 18 : 9;
        int iceCount = keepCyro ? 18 : 9;

        for (int i = 0; i < flameCount; i++) {
            double a = w.random.nextDouble()*Math.PI*2, r = w.random.nextDouble()+0.2;
            w.spawnParticles(ParticleTypes.FLAME, cx+Math.cos(a)*r, cy+(w.random.nextDouble()-0.5)*1.5, cz+Math.sin(a)*r, 1, Math.cos(a)*0.05,0.1,Math.sin(a)*0.05,0.02);
        }
        for (int i = 0; i < iceCount; i++) {
            double a = w.random.nextDouble()*Math.PI*2, r = w.random.nextDouble()*0.8+0.2;
            w.spawnParticles(ParticleTypes.ITEM_SNOWBALL, cx+Math.cos(a)*r, cy+(w.random.nextDouble()-0.5)*1.2, cz+Math.sin(a)*r, 1, Math.cos(a)*0.03,0.05,Math.sin(a)*0.03,0.04);
        }
        for (int i = 0; i < 3; i++) {
            double px = cx+(w.random.nextDouble()-0.5)*0.4, pz = cz+(w.random.nextDouble()-0.5)*0.4;
            w.spawnParticles(ParticleTypes.SMALL_FLAME, px, cy+0.2, pz, 1, 0, 0.15, 0, 0.01);
        }
        for (int i = 0; i < 5; i++) {
            double a = w.random.nextDouble()*Math.PI*2, s = 0.05+w.random.nextDouble()*0.1;
            w.spawnParticles(ParticleTypes.SNOWFLAKE, cx+Math.cos(a)*0.3, cy+(w.random.nextDouble()-0.5)*0.5, cz+Math.sin(a)*0.3, 1, Math.cos(a)*s, w.random.nextDouble()*0.1, Math.sin(a)*s, 0.01);
        }
    }

    private void playSound(ServerWorld w, LivingEntity e, float dmg) {
        double x = e.getX(), y = e.getY()+e.getHeight()*0.5, z = e.getZ();
        if (dmg >= 3.0f) {
            w.playSound(null, x, y, z, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.8f, 1.2f);
        } else {
            w.playSound(null, x, y, z, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.PLAYERS, 0.5f, 1.5f);
        }
    }

    @Override
    protected void onReact(LivingEntity entity, ServerWorld world, Map<StatusEffect, Long> times) {
        // Melt 的逻辑完全由覆写的 tickEntity 处理，此处无需操作
    }
}
