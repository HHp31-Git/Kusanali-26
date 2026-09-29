package com.kusanali.define.item.weapon;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public abstract class GreatSword extends ToolItem implements Vanishable {
    private static final UUID ATTACK_DAMAGE_MODIFIER_ID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    private static final UUID ATTACK_SPEED_MODIFIER_ID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");

    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;
    private final float attackDamage;

    public GreatSword(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings) {
        super(material, settings);
        this.attackDamage = attackDamage;

        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();

        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier",
                        this.attackDamage, EntityAttributeModifier.Operation.ADDITION));

        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED,
                new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier",
                        attackSpeed, EntityAttributeModifier.Operation.ADDITION));

        this.attributeModifiers = builder.build();
    }

    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        if (state.isOf(Blocks.COBWEB)) {
            return 12.0F;
        }
        return 1.0F;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, (e) -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));

        if (attacker instanceof PlayerEntity player) {
            // 暴击判定：在空中下落时攻击
            boolean isCrit = !player.isOnGround() && player.fallDistance > 0.0F &&
                    !player.isTouchingWater() && !player.hasVehicle();

            if (isCrit) {
                // 暴击额外1点伤害
                target.damage(player.getDamageSources().playerAttack(player), 1.0F);

                // 自带抢夺1（可与附魔叠加）
                int lootingLevel = EnchantmentHelper.getLevel(Enchantments.LOOTING, stack);
                int totalLooting = 1 + lootingLevel;

                // 使用自定义NBT键存储抢夺等级
                target.writeCustomDataToNbt(new NbtCompound() {{
                    putInt("greatsword_looting", totalLooting);
                }});
            }
        }

        return true;
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state,
                            BlockPos pos, LivingEntity miner) {
        if (!world.isClient && state.getHardness(world, pos) != 0.0F) {
            stack.damage(2, miner, (e) -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }
        return true;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            return this.attributeModifiers;
        }
        return super.getAttributeModifiers(slot);
    }

    // 获取大剑的抢夺等级
    public static int getLootingBonus(LivingEntity target) {
        NbtCompound nbt = new NbtCompound();
        target.writeCustomDataToNbt(nbt);
        if (nbt.contains("greatsword_looting")) {
            return nbt.getInt("greatsword_looting");
        }
        return 0;
    }

    // 清除抢夺标记
    public static void clearLootingBonus(LivingEntity target) {
        NbtCompound nbt = new NbtCompound();
        target.writeCustomDataToNbt(nbt);
        nbt.remove("greatsword_looting");
        target.readCustomDataFromNbt(nbt);
    }
}