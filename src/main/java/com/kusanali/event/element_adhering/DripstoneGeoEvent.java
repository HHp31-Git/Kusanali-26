package com.kusanali.event.element_adhering;

import com.kusanali.define.event.ElementAdhering;
import com.kusanali.register.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.world.World;

public class DripstoneGeoEvent extends ElementAdhering {

    @Override
    protected StatusEffect getTargetEffect() {
        return ModEffects.GEO;
    }

    @Override
    protected boolean checkCondition(LivingEntity entity, World world) {
        if (world.isClient()) return false;

        // 获取实体最近受到的伤害来源
        DamageSource recentSource = entity.getRecentDamageSource();
        if (recentSource == null) return false;

        // 匹配钟乳石掉落伤害 或 石笋接触伤害
        return recentSource.isOf(DamageTypes.FALLING_STALACTITE)
                || recentSource.isOf(DamageTypes.STALAGMITE);
    }
}
