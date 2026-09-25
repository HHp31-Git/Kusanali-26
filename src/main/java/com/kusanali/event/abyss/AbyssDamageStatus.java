package com.kusanali.event.abyss;

import com.kusanali.register.ModDamageTypes;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

import java.util.random.RandomGenerator;

public class AbyssDamageStatus {
    private static final RandomGenerator RNG = RandomGenerator.getDefault();

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (source.isOf(ModDamageTypes.ABYSS) && !entity.getWorld().isClient) {
                applyAbyssEffect(entity);
            }
            return true;
        });
    }

    private static void applyAbyssEffect(LivingEntity entity) {
        int roll = RNG.nextInt(100); // 0-99

        if (roll < 30) {
            entity.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.SLOWNESS, 40, 2, false, false, false
            ));
        } else if (roll < 60) {
            entity.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.WEAKNESS, 40, 2, false, false, false
            ));
        }
    }
}
