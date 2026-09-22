package com.kusanali.mixin.rule;

import com.kusanali.register.ModGameRules;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieEntity.class)
public abstract class ZombieSpeedMixin {
    @Unique
    private long kusanali_cooldownEnd = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void kusanali_onTick(CallbackInfo ci) {
        ZombieEntity zombie = (ZombieEntity)(Object)this;

        if (isTorchZombie(zombie)) {
            return;
        }
        if (!zombie.getWorld().getGameRules().getBoolean(ModGameRules.REINFORCE)) {
            return;
        }

        long time = zombie.getWorld().getTime();

        if (time < kusanali_cooldownEnd) {
            if (zombie.hasStatusEffect(StatusEffects.SPEED)) {
                zombie.removeStatusEffect(StatusEffects.SPEED);
            }
            return;
        }

        if (zombie.getTarget() != null && zombie.getTarget().isAlive()) {
            zombie.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.SPEED,
                    40,
                    3,
                    false,
                    false,
                    true
            ));
        }
        else if (zombie.hasStatusEffect(StatusEffects.SPEED)) {
            zombie.removeStatusEffect(StatusEffects.SPEED);
        }
    }
    @Inject(method = "tryAttack", at = @At("RETURN"))
    private void kusanali_onAttack(net.minecraft.entity.Entity target, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() != true) return;

        ZombieEntity zombie = (ZombieEntity)(Object)this;
        if (isTorchZombie(zombie)) return;

        if (zombie.hasStatusEffect(StatusEffects.SPEED)) {
            zombie.removeStatusEffect(StatusEffects.SPEED);
        }
        kusanali_cooldownEnd = zombie.getWorld().getTime() + 400;
    }
    @Unique
    private static boolean isTorchZombie(ZombieEntity zombie) {
        if (!zombie.getWorld().getGameRules().getBoolean(ModGameRules.REINFORCE)) {
            return true;
        }
        if (zombie.getMainHandStack().getItem() != Items.TORCH) {
            return true;
        }
        return !zombie.getMainHandStack().hasEnchantments();
    }
}
