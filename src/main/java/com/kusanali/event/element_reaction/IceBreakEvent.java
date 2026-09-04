package com.kusanali.event.element_reaction;

import com.kusanali.datagenerator.DamageTypeTagProvider;
import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

public class IceBreakEvent {
    private static final Set<UUID> REENTERING = Collections.newSetFromMap(new WeakHashMap<>());

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity.getWorld().isClient()) return true;
            if (!entity.hasStatusEffect(ModEffects.FREEZING)) return true;
            if (!source.isIn(DamageTypeTagProvider.PHYSICAL)) return true;

            UUID uuid = entity.getUuid();
            if (REENTERING.contains(uuid)) return true;

            entity.removeStatusEffect(ModEffects.FREEZING);
            spawnIceBreakParticles(entity);

            REENTERING.add(uuid);
            try {
                entity.damage(source, 4.0f);
            } finally {
                Objects.requireNonNull(entity.getServer()).execute(() -> REENTERING.remove(uuid));
            }

            return false;
        });
    }
    private static void spawnIceBreakParticles(LivingEntity entity) {
        if (!(entity.getWorld() instanceof ServerWorld serverWorld)) return;

        double x = entity.getX();
        double y = entity.getY() + entity.getHeight() * 0.5;
        double z = entity.getZ();

        serverWorld.spawnParticles(
                ParticleTypes.ITEM_SNOWBALL,
                x, y, z,
                12,
                0.8,
                0.5,
                0.8,
                0.25
        );

        serverWorld.spawnParticles(
                ParticleTypes.WHITE_ASH,
                x, y, z,
                8,
                1.0,
                0.6,
                1.0,
                0.08
        );
        serverWorld.playSound(
                null,
                x, y, z,
                net.minecraft.sound.SoundEvents.BLOCK_GLASS_BREAK,
                net.minecraft.sound.SoundCategory.BLOCKS,
                1.0f,
                1.5f
        );
    }
}
