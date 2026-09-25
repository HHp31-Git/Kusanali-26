package com.kusanali.event.element_adhering;

import com.kusanali.define.event.ElementAdhering;
import com.kusanali.register.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.world.World;

import java.util.Objects;

public class PyroEvent extends ElementAdhering {
    @Override
    protected StatusEffect getTargetEffect() {
        return ModEffects.PYRO;
    }

    @Override
    protected boolean checkCondition(LivingEntity entity, World world) {
        return isFireDamage(Objects.requireNonNull(entity.getRecentDamageSource()));
    }

    private static boolean isFireDamage(DamageSource source) {
        return source.isOf(DamageTypes.IN_FIRE)
                || source.isOf(DamageTypes.ON_FIRE)
                || source.isOf(DamageTypes.LAVA)
                || source.isOf(DamageTypes.HOT_FLOOR)
                || source.isOf(DamageTypes.PLAYER_EXPLOSION)
                || source.isOf(DamageTypes.EXPLOSION)
                || source.isOf(DamageTypes.FIREBALL)
                || source.isOf(DamageTypes.FIREWORKS)
                || source.isOf(DamageTypes.UNATTRIBUTED_FIREBALL);
    }
}
