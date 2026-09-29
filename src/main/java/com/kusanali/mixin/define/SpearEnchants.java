package com.kusanali.mixin.define;

import com.kusanali.define.item.weapon.SpearItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class SpearEnchants {

    @Unique
    @Final
    public EnchantmentTarget type;

    @Inject(method = "isAcceptableItem", at = @At("HEAD"), cancellable = true)
    private void allowSpearEnchants(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() instanceof SpearItem spear) {
            Enchantment self = (Enchantment)(Object)this;

            // 排除横扫之刃
            if (self == Enchantments.SWEEPING) {
                cir.setReturnValue(false);
                return;
            }

            // 如果是突进模式，允许穿刺附魔
            if (spear.getMode() == SpearItem.AttackMode.CHARGE && self == Enchantments.IMPALING) {
                cir.setReturnValue(true);
                return;
            }

            // 其他剑类附魔保持兼容
            cir.setReturnValue(
                    this.type == EnchantmentTarget.WEAPON ||
                            this.type == EnchantmentTarget.BREAKABLE
            );
        }
    }
}