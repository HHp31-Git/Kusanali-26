package com.kusanali.mixin.rule;

import com.kusanali.register.ModGameRules;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieEntity.class)
public abstract class ZombieEquipmentMixin {

    @Inject(method = "initEquipment", at = @At("TAIL"))
    private void kusanali_onInitEquipment(Random random, LocalDifficulty localDifficulty, CallbackInfo ci) {
        ZombieEntity zombie = (ZombieEntity)(Object)this;

        if (!zombie.getWorld().getGameRules().getBoolean(ModGameRules.REINFORCE)) {
            return;
        }

        if (random.nextFloat() >= 0.05f) {
            return;
        }

        ItemStack torch = new ItemStack(Items.TORCH);
        torch.addEnchantment(Enchantments.FIRE_ASPECT, 1);
        torch.addEnchantment(Enchantments.KNOCKBACK, 1);
        zombie.equipStack(EquipmentSlot.MAINHAND, torch);

        ItemStack helmet = new ItemStack(Items.LEATHER_HELMET);
        ItemStack chestplate = new ItemStack(Items.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Items.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Items.LEATHER_BOOTS);

        int red = 0xFF0000;
        helmet.getOrCreateNbt().putInt("color", red);
        chestplate.getOrCreateNbt().putInt("color", red);
        leggings.getOrCreateNbt().putInt("color", red);
        boots.getOrCreateNbt().putInt("color", red);

        zombie.equipStack(EquipmentSlot.HEAD, helmet);
        zombie.equipStack(EquipmentSlot.CHEST, chestplate);
        zombie.equipStack(EquipmentSlot.LEGS, leggings);
        zombie.equipStack(EquipmentSlot.FEET, boots);

        zombie.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.0f);
        zombie.setEquipmentDropChance(EquipmentSlot.HEAD, 0.0f);
        zombie.setEquipmentDropChance(EquipmentSlot.CHEST, 0.0f);
        zombie.setEquipmentDropChance(EquipmentSlot.LEGS, 0.0f);
        zombie.setEquipmentDropChance(EquipmentSlot.FEET, 0.0f);
    }
}
