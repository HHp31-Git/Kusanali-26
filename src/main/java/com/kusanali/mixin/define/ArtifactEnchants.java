package com.kusanali.mixin.define;

import com.kusanali.define.item.weapon.Artifact;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class ArtifactEnchants {

    @Inject(method = "isAcceptableItem", at = @At("RETURN"), cancellable = true)
    private void allowArtifactEnchants(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!(stack.getItem() instanceof Artifact)) {
            return;
        }

        Enchantment self = (Enchantment)(Object)this;

        // 仅允许耐久和经验修补
        if (self == Enchantments.UNBREAKING || self == Enchantments.MENDING) {
            cir.setReturnValue(true);
            return;
        }

        // 其他附魔都不允许
        cir.setReturnValue(false);
    }
}