package com.kusanali.event.element_adhering;

import com.kusanali.define.event.ElementAdhering;
import com.kusanali.register.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ElectroEvent extends ElementAdhering {

    @Override protected StatusEffect getTargetEffect() { return ModEffects.ELECTRO; }

    @Override
    protected boolean checkCondition(LivingEntity entity, World world) {
        if (!world.isThundering()) return false;
        BlockPos pos = entity.getBlockPos();
        return world.isSkyVisible(pos);
    }
}
