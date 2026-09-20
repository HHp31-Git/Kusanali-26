package com.kusanali.mixin.render;

import com.kusanali.server.PackControl;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class BackGroundOverlay {
    @Unique
    private static final Identifier STATIC_BG =
            new Identifier("kusanali", "textures/gui/title/background/panorama_overlay.png");

    @Unique
    private static final int BW = 3840, BH = 2160;

    @Inject(
            method = "render",
            at = @At("HEAD")
    )
    private void drawStaticBg(DrawContext ctx, int mx, int my, float delta, CallbackInfo ci) {
        if (PackControl.fancyTitleEnabled()) return;
        float fadeAlpha = 1.0F;

        int sw = ctx.getScaledWindowWidth();
        int sh = ctx.getScaledWindowHeight();
        float sr = (float) sw / sh;
        float ir = (float) BW / BH;

        int rw, rh;
        if (sr >= ir) {
            // 屏幕相对更宽：源区域横向取满，纵向裁剪
            rw = BW;
            rh = Math.round(BW / sr);
        } else {
            // 屏幕相对更高：源区域纵向取满，横向裁剪
            rh = BH;
            rw = Math.round(BH * sr);
        }

        int u = (BW - rw) / 2;
        int v = (BH - rh) / 2;

        // 目标区域 = 整个屏幕（sw, sh）；源区域 = 居中裁剪的 (u, v, rw, rh)
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, fadeAlpha);
        ctx.drawTexture(STATIC_BG, 0, 0, sw, sh, u, v, rw, rh, BW, BH);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        ctx.fill(0, 0, sw, sh, 0);
    }
}