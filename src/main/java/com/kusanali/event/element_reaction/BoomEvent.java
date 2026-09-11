package com.kusanali.event.element_reaction;

import com.kusanali.define.event.ElementReaction;
import com.kusanali.entity.ModEntities;
import com.kusanali.entity.custom.DendroSeedEntity;
import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class BoomEvent extends ElementReaction {
    @Override protected int getCooldown() { return 12; }
    @Override protected List<StatusEffect> getParticipatingEffects() {
        return List.of(ModEffects.DENDRO, ModEffects.HYDRO);
    }

    @Override
    protected void onReact(LivingEntity entity, ServerWorld world, Map<StatusEffect, Long> times) {
        entity.damage(ModDamageTypes.reaction_type_2(world), 1.0f);
        spawnSeeds(world, entity);
        spawnBloomParticles(world, entity);
    }

    private void spawnSeeds(ServerWorld world, LivingEntity entity) {
        Vec3d center = entity.getPos();
        int seedCount = 1 + (world.random.nextFloat() < 0.3f ? 1 : 0);
        int spawned = 0;
        for (int attempt = 0; spawned < seedCount && attempt < seedCount * 10; attempt++) {
            double angle = world.random.nextDouble() * Math.PI * 2;
            double radius = 0.5 + world.random.nextDouble();
            double cx = center.x + Math.cos(angle) * radius;
            double cz = center.z + Math.sin(angle) * radius;
            double groundY = findGroundY(world, cx, center.y, cz);
            if (groundY == Double.NEGATIVE_INFINITY) continue;
            double sy = groundY + 0.5;
            BlockPos sp = BlockPos.ofFloored(cx, sy, cz);
            if (!world.getBlockState(sp).isAir() || !world.getBlockState(sp.up()).isAir()) continue;
            DendroSeedEntity seed = new DendroSeedEntity(ModEntities.DENDRO_SEED, world);
            seed.setPosition(cx, sy, cz);
            world.spawnEntity(seed);
            spawned++;
        }
    }

    private double findGroundY(ServerWorld w, double x, double sy, double z) {
        for (int dy = 0; dy >= -5; dy--) {
            BlockPos p = new BlockPos((int)Math.floor(x), (int)Math.floor(sy) + dy, (int)Math.floor(z));
            if (!w.getBlockState(p).isAir()) return p.getY() + 1.0;
        }
        return Double.NEGATIVE_INFINITY;
    }

    private void spawnBloomParticles(ServerWorld w, LivingEntity e) {
        double cx = e.getX(), cy = e.getY() + e.getHeight()*0.5, cz = e.getZ();
        for (int i = 0; i < 20; i++) {
            double a = w.random.nextDouble() * Math.PI * 2, r = w.random.nextDouble() * 1.5;
            w.spawnParticles(ParticleTypes.HAPPY_VILLAGER, cx+Math.cos(a)*r, cy+(w.random.nextDouble()-0.5), cz+Math.sin(a)*r, 1, Math.cos(a)*0.05, w.random.nextDouble()*0.05, Math.sin(a)*0.05, 0.05);
        }
        for (int i = 0; i < 10; i++) {
            double a = w.random.nextDouble() * Math.PI * 2, r = w.random.nextDouble();
            w.spawnParticles(ParticleTypes.FALLING_WATER, cx+Math.cos(a)*r, cy+0.2, cz+Math.sin(a)*r, 1, Math.cos(a)*0.03, 0.1, Math.sin(a)*0.03, 0.02);
        }
        for (int ring = 0; ring < 2; ring++) {
            double rr = 0.5 + ring * 0.8;
            for (int i = 0; i < 8; i++) {
                double a = (Math.PI*2/8)*i;
                w.spawnParticles(ParticleTypes.CLOUD, cx+Math.cos(a)*rr, cy-0.3, cz+Math.sin(a)*rr, 1, 0, 0.02, 0, 0.02);
            }
        }
        w.spawnParticles(ParticleTypes.CRIT, cx, cy, cz, 5, 0.2, 0.2, 0.2, 0.1);
    }
}
