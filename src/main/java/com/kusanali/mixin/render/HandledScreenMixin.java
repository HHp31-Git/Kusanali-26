package com.kusanali.mixin.render;

import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(InventoryScreen.class)
public abstract class HandledScreenMixin {

    @ModifyArg(
            method = "drawForeground",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;" +
                            "drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I"),
            index = 4 // color 参数的位置
    )
    private int titleColor(int color) {
        return 0x00ff48; // 0xRRGGBB，改成想要的颜色
    }
}
