package com.kusanali.mixin.rule;

import com.kusanali.register.ModGameRules;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WitchEntity.class)
public abstract class WitchFireballMixin {

    @Inject(
            method = "attack(Lnet/minecraft/entity/LivingEntity;F)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void kusanali_maybeThrowFireball(LivingEntity target, float pullProgress, CallbackInfo ci) {
        WitchEntity witch = (WitchEntity)(Object)this;
        World world = witch.getWorld();

        if (world.isClient) return;
        if (!(world instanceof ServerWorld sw)) return;

        if (!sw.getGameRules().getBoolean(ModGameRules.REINFORCE)) return;

        if (witch.getRandom().nextFloat() >= 0.10F) return;

        Vec3d eye = witch.getEyePos();
        Vec3d dest = target.getEyePos();
        Vec3d dir = dest.subtract(eye).normalize().multiply(1.5);

        SmallFireballEntity fireball = new SmallFireballEntity(
                world,
                witch,
                dir.x, dir.y, dir.z
        );
        fireball.setPosition(eye.x, eye.y, eye.z);
        fireball.setNoGravity(true);
        world.spawnEntity(fireball);

        ci.cancel();
    }
}
