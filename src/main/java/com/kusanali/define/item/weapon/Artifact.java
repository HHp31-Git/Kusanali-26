package com.kusanali.define.item.weapon;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Vanishable;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * 法器基类
 * 所有伤害附带特殊damageType和10秒效果
 */
public abstract class Artifact extends Item implements Vanishable {

    protected final float attackDamage;
    protected final StatusEffect statusEffect;
    protected final int effectDuration; // 单位：tick，默认200tick=10秒
    protected final int effectAmplifier;

    // 冷却时长（单位：tick，20tick=1秒）
    protected final int leftClickCooldown;
    protected final int rightClickCooldown;

    // 蓄力时长（单位：tick）
    protected final int chargeTime;

    /**
     * 全参数构造
     */
    public Artifact(float attackDamage, StatusEffect statusEffect,
                        int effectAmplifier, int effectDuration,
                        int leftClickCooldown, int rightClickCooldown,
                        int chargeTime, Settings settings) {
        super(settings);
        this.attackDamage = attackDamage;
        this.statusEffect = statusEffect;
        this.effectAmplifier = effectAmplifier;
        this.effectDuration = effectDuration;
        this.leftClickCooldown = leftClickCooldown;
        this.rightClickCooldown = rightClickCooldown;
        this.chargeTime = chargeTime;
    }

    /**
     * 获取法器的伤害类型标识符
     */
    public String getDamageTypeId() {
        return "artifact_damage";
    }

    /**
     * 获取基础伤害值
     */
    public float getAttackDamage() {
        return this.attackDamage;
    }

    /**
     * 获取左键冷却时长
     */
    public int getLeftClickCooldown() {
        return this.leftClickCooldown;
    }

    /**
     * 获取右键冷却时长
     */
    public int getRightClickCooldown() {
        return this.rightClickCooldown;
    }

    /**
     * 获取蓄力时长
     */
    public int getChargeTime() {
        return this.chargeTime;
    }

    /**
     * 左键攻击行为
     * @param chargeProgress 蓄力进度（0.0~1.0），左键通常为1.0
     */
    public abstract ActionResult leftClickAction(World world, PlayerEntity player,
                                                 Hand hand, float chargeProgress);

    /**
     * 右键蓄力攻击行为
     * @param chargeProgress 蓄力进度（0.0~1.0）
     */
    public abstract TypedActionResult<ItemStack> rightClickAction(World world, PlayerEntity player,
                                                                  Hand hand, float chargeProgress);

    /**
     * 应用状态效果到目标
     */
    protected void applyStatusEffect(LivingEntity target, LivingEntity attacker) {
        if (statusEffect != null) {
            target.addStatusEffect(new StatusEffectInstance(
                    statusEffect,
                    effectDuration,
                    effectAmplifier,
                    false,
                    true,
                    true
            ));
        }
    }

    /**
     * 设置冷却
     */
    protected void setCooldown(PlayerEntity player, int cooldown) {
        player.getItemCooldownManager().set(this, cooldown);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user,
                                    LivingEntity entity, Hand hand) {
        if (hand == Hand.MAIN_HAND && !user.getItemCooldownManager().isCoolingDown(this)) {
            ActionResult result = leftClickAction(user.getWorld(), user, hand, 1.0F);
            setCooldown(user, leftClickCooldown);
            return result;
        }
        return ActionResult.PASS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!user.getItemCooldownManager().isCoolingDown(this)) {
            user.setCurrentHand(hand);
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
        return TypedActionResult.fail(user.getStackInHand(hand));
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!world.isClient && user instanceof PlayerEntity player) {
            // 计算蓄力进度
            int usedTicks = this.getMaxUseTime(stack) - remainingUseTicks;
            float chargeProgress = Math.min(1.0F, (float) usedTicks / this.chargeTime);

            // 执行右键蓄力攻击
            TypedActionResult<ItemStack> result = rightClickAction(world, player,
                    player.getActiveHand(), chargeProgress);

            // 设置冷却
            setCooldown(player, rightClickCooldown);
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return this.chargeTime;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // 应用状态效果
        applyStatusEffect(target, attacker);

        // 耐久消耗
        EquipmentSlot slot = attacker instanceof PlayerEntity player ?
                (player.getActiveHand() == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND) :
                EquipmentSlot.MAINHAND;

        stack.damage(1, attacker, (e) -> e.sendEquipmentBreakStatus(slot));

        return true;
    }
}