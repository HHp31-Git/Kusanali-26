package com.kusanali.mixin.entity;

import com.kusanali.datagenerator.EntityTypeTagsProvider;
import com.kusanali.register.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LatentElseMobAdhering {
    @Inject(method = "tick", at = @At("HEAD"))
    private void kusanali_applyLatentToNonMob(CallbackInfo ci) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (self.getWorld().isClient()) return;

        if (self instanceof MobEntity) return;
        if (self.getCommandTags().contains("kusanali.latent_applied")) return;

        var type = self.getType();
        if (type.isIn(EntityTypeTagsProvider.PYRO_ENTITY)) {
            self.addStatusEffect(new StatusEffectInstance(
                    ModEffects.PYRO, -1, 0, false, false, false));
        } else if (type.isIn(EntityTypeTagsProvider.HYDRO_ENTITY)) {
            self.addStatusEffect(new StatusEffectInstance(
                    ModEffects.HYDRO, -1, 0, false, false, false));
        } else if (type.isIn(EntityTypeTagsProvider.ANEMO_ENTITY)) {
            self.addStatusEffect(new StatusEffectInstance(
                    ModEffects.ANEMO, -1, 0, false, false, false));
        } else if (type.isIn(EntityTypeTagsProvider.GEO_ENTITY)) {
            self.addStatusEffect(new StatusEffectInstance(
                    ModEffects.GEO, -1, 0, false, false, false));
        } else if (type.isIn(EntityTypeTagsProvider.ELECTRO_ENTITY)) {
            self.addStatusEffect(new StatusEffectInstance(
                    ModEffects.ELECTRO, -1, 0, false, false, false));
        } else if (type.isIn(EntityTypeTagsProvider.DENDRO_ENTITY)) {
            self.addStatusEffect(new StatusEffectInstance(
                    ModEffects.DENDRO, -1, 0, false, false, false));
        } else if (type.isIn(EntityTypeTagsProvider.CYRO_ENTITY)) {
            self.addStatusEffect(new StatusEffectInstance(
                    ModEffects.CYRO, -1, 0, false, false, false));
        }
        self.addCommandTag("kusanali.latent_applied");
    }
}
