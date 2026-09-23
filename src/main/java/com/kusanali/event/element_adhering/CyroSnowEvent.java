package com.kusanali.event.element_adhering;

import com.kusanali.define.event.ElementAdhering;
import com.kusanali.register.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CyroSnowEvent extends ElementAdhering {

    @Override protected StatusEffect getTargetEffect() { return ModEffects.CYRO; }

    @Override
    protected boolean checkCondition(LivingEntity entity, World world) {
        if (!world.isRaining()) return false;
        BlockPos pos = entity.getBlockPos();
        if (!world.getBiome(pos).value().isCold(pos)) return false;
        return world.isSkyVisible(pos);
    }
}
