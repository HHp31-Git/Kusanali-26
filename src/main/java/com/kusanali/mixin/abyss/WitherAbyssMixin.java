package com.kusanali.mixin.abyss;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Objects;

import static com.kusanali.register.ModDamageTypes.abyss;

@Mixin(LivingEntity.class)
public abstract class WitherAbyssMixin {
    @ModifyArg(
            method = "damage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;" +
                            "applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V"
            ),
            index = 0
    )
    private DamageSource kusanali_replaceWitherDamage(DamageSource source) {
        if (!shouldConvert(source)) return source;
        World world = Objects.requireNonNull(source.getAttacker()).getWorld();
        return abyss(world);
    }

    @Unique
    private boolean shouldConvert(DamageSource source) {
        if (source.getName().equals("wither")) return false;

        if (source.getAttacker() != null) {
            EntityType<?> type = source.getAttacker().getType();
            return type == EntityType.WITHER_SKELETON || type == EntityType.WITHER;
        }
        return false;
    }
}
