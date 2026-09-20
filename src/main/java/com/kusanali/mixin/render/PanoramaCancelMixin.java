package com.kusanali.mixin.render;

import com.kusanali.server.PackControl;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RotatingCubeMapRenderer.class)
public abstract class PanoramaCancelMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void kusanali$skipPanorama(CallbackInfo ci) {
        if (PackControl.fancyTitleEnabled()) return;
        ci.cancel();
    }
}