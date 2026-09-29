package com.kusanali.mixin.define;

import com.kusanali.define.item.weapon.SpearItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class SpearDefine {
    @Inject(method = "spawnSweepAttackParticles", at = @At("HEAD"), cancellable = true)
    private void disableSweepForSpear(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        ItemStack mainHand = player.getMainHandStack();

        if (mainHand.getItem() instanceof SpearItem) {
            ci.cancel();
        }
    }
}
