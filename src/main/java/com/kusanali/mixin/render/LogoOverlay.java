package com.kusanali.mixin.render;

import com.kusanali.server.PackControl;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LogoDrawer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LogoDrawer.class)
public abstract class LogoOverlay {

    /**
     * 完全阻止 LogoDrawer.draw(DrawContext, int, float) 的执行
     * 即：既不渲染 Minecraft 标题 Logo，也不渲染 Edition 文字
     */
    @Inject(
            method = "draw(Lnet/minecraft/client/gui/DrawContext;IF)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventLogoRendering(DrawContext ctx, int screenWidth, float alpha, CallbackInfo ci) {
        if (PackControl.fancyTitleEnabled()) return;
        ci.cancel();
    }
}