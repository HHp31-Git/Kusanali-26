package com.kusanali.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.kusanali.register.ModEffects.BASE_ELEMENTS_ARRAY;

@Mixin(ProjectileEntity.class)
public abstract class WitchAttackMixin {
    @Inject(
            method = "onEntityHit",
            at = @At("HEAD")
    )
    private void kusanali$witchArrowElement(EntityHitResult hitResult, CallbackInfo ci) {
        ProjectileEntity self = (ProjectileEntity) (Object) this;

        if (!(self instanceof ArrowEntity arrow)) {
            return;
        }

        Entity owner = arrow.getOwner();
        if (!(owner instanceof WitchEntity)) {
            return;
        }

        Entity hitEntity = hitResult.getEntity();
        if (!(hitEntity instanceof LivingEntity target)) {
            return;
        }

        if (self.getWorld().isClient) {
            return;
        }

        Random random = self.getWorld().getRandom();
        StatusEffect effect = BASE_ELEMENTS_ARRAY[random.nextInt(BASE_ELEMENTS_ARRAY.length)];

        target.addStatusEffect(
                new StatusEffectInstance(
                        effect,
                        200,
                        0,
                        false,
                        true,
                        true
                )
        );
    }
}
