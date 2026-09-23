package com.kusanali.event.element_adhering;

import com.kusanali.define.event.ElementAdhering;
import com.kusanali.register.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HydroEvent extends ElementAdhering {

    @Override
    protected StatusEffect getTargetEffect() { return ModEffects.HYDRO; }

    @Override
    protected boolean checkCondition(LivingEntity entity, World world) {
        return isInWaterOrWaterlogged(entity, world) || isInRain(entity, world);
    }

    private static boolean isInWaterOrWaterlogged(LivingEntity entity, World world) {
        if (entity.isTouchingWater()) return true;
        BlockPos pos = entity.getBlockPos();
        if (world.getFluidState(pos).isOf(Fluids.WATER)) return true;
        BlockPos eyePos = BlockPos.ofFloored(entity.getEyePos());
        return world.getFluidState(eyePos).isOf(Fluids.WATER);
    }

    private static boolean isInRain(LivingEntity entity, World world) {
        if (!world.isRaining()) return false;
        BlockPos pos = entity.getBlockPos();
        if (!world.getBiome(pos).value().hasPrecipitation()) return false;
        return world.isSkyVisible(pos);
    }
}
