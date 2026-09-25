package com.kusanali.event.element_adhering;

import com.kusanali.define.event.ElementAdhering;
import com.kusanali.register.ModEffects;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.world.World;

public class BerryBushDendroEvent extends ElementAdhering {
    @Override
    protected StatusEffect getTargetEffect() {
        return ModEffects.DENDRO;
    }

    @Override
    protected boolean checkCondition(LivingEntity entity, World world) {
        if (entity.getRecentDamageSource() == null) return false;

        DamageSource source = entity.getRecentDamageSource();
        Entity attacker = source.getAttacker();

        if (attacker instanceof LivingEntity livingAttacker) {
            return world.getBlockState(livingAttacker.getBlockPos()).isOf(Blocks.SWEET_BERRY_BUSH);
        }
        return false;
    }
}
