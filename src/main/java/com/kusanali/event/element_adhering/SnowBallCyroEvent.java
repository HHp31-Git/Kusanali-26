package com.kusanali.event.element_adhering;

import com.kusanali.define.event.ElementAdhering;
import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class SnowBallCyroEvent extends ElementAdhering {
    @Override
    protected StatusEffect getTargetEffect() {
        return ModEffects.CYRO;
    }

    @Override
    protected boolean checkCondition(LivingEntity entity, World world) {
        return entity.getRecentDamageSource() != null
                && entity.getRecentDamageSource().getSource() instanceof SnowballEntity;
    }
}
