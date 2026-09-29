package com.kusanali.mixin.define;

import com.kusanali.define.item.weapon.GreatSword;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class GreatSwordEnchants {

    @Unique
    public EnchantmentTarget type;

    @Inject(method = "isAcceptableItem", at = @At("HEAD"), cancellable = true)
    private void allowGreatswordEnchants(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!(stack.getItem() instanceof GreatSword)) {
            return;
        }

        Enchantment self = (Enchantment)(Object)this;

        // 排除效率、时运
        if (self == Enchantments.EFFICIENCY || self == Enchantments.FORTUNE) {
            cir.setReturnValue(false);
            return;
        }

        // 排除横扫之刃
        if (self == Enchantments.SWEEPING) {
            cir.setReturnValue(false);
            return;
        }

        // 允许武器类和可破坏类附魔
        cir.setReturnValue(
                this.type == EnchantmentTarget.WEAPON ||
                        this.type == EnchantmentTarget.BREAKABLE
        );
    }
}