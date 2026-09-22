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
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class BoomEvent extends ElementReaction {
    @Override protected int getCooldown() { return 20; }
    @Override protected List<StatusEffect> getParticipatingEffects() { return List.of(ModEffects.DENDRO, ModEffects.ELECTRO); }
    @Override protected void onReact(LivingEntity entity, ServerWorld world, Map<StatusEffect, Long> times) {
        entity.damage(ModDamageTypes.reaction_type_2(world), 1.5f);
        Vec3d pos = entity.getPos();
        DendroSeedEntity seed = new DendroSeedEntity(ModEntities.DENDRO_SEED, world);
        seed.setPosition(pos.x, pos.y + entity.getHeight() * 0.5, pos.z);
        world.spawnEntity(seed);

        double cx = pos.x, cy = pos.y + entity.getHeight() * 0.5, cz = pos.z;
        world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, cx, cy, cz, 10, 0.4, 0.4, 0.4, 0.1);
        world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, cx, cy, cz, 6, 0.3, 0.3, 0.3, 0.05);
    }
}
