package com.kusanali.mixin.entity;

import com.kusanali.datagenerator.EntityTypeTagsProvider;
import com.kusanali.register.ModEffects;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class LatentAdhering {
    @Inject(
            method = "initialize",
            at = @At("TAIL")
    )
    private void kusanali_applyLatentElement(
            ServerWorldAccess world,
            LocalDifficulty difficulty,
            SpawnReason spawnReason,
            @Nullable EntityData entityData,
            @Nullable NbtCompound entityNbt,
            CallbackInfoReturnable<EntityData> cir
    ) {
        MobEntity self = (MobEntity)(Object)this;
        if (world.isClient()) return;

        var type = self.getType();
        if (type.isIn(EntityTypeTagsProvider.PYRO_ENTITY)) {
            self.addStatusEffect(new StatusEffectInstance(
                    ModEffects.PYRO, -1, 0, false, true, true));
        } else if (type.isIn(EntityTypeTagsProvider.HYDRO_ENTITY)) {
            self.addStatusEffect(new StatusEffectInstance(
                    ModEffects.HYDRO, -1, 0, false, true, true));
        } else if (type.isIn(EntityTypeTagsProvider.ANEMO_ENTITY)) {
            self.addStatusEffect(new StatusEffectInstance(
                    ModEffects.ANEMO, -1, 0, false, true, true));
        } else if (type.isIn(EntityTypeTagsProvider.GEO_ENTITY)) {
            self.addStatusEffect(new StatusEffectInstance(
                    ModEffects.GEO, -1, 0, false, true, true));
        } else if (type.isIn(EntityTypeTagsProvider.ELECTRO_ENTITY)) {
            self.addStatusEffect(new StatusEffectInstance(
                    ModEffects.ELECTRO, -1, 0, false, true, true));
        } else if (type.isIn(EntityTypeTagsProvider.DENDRO_ENTITY)) {
            self.addStatusEffect(new StatusEffectInstance(
                    ModEffects.DENDRO, -1, 0, false, true, true));
        } else if (type.isIn(EntityTypeTagsProvider.CYRO_ENTITY)) {
            self.addStatusEffect(new StatusEffectInstance(
                    ModEffects.CYRO, -1, 0, false, true, true));
        }
    }
}
