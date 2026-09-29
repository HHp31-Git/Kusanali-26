package com.kusanali.specialitem.combats;

import com.kusanali.define.item.weapon.Artifact;
import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import com.kusanali.register.ModItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

/**
 * 浮梦法器 —— 兼具范围魔法阵与蓄力重击的特殊法器
 *
 * <p><b>左键</b>：向前方生成 4 个魔法阵，对范围内敌人造成 7 点伤害 + 10 秒 Dendro 效果。</p>
 * <p><b>右键</b>：蓄力后对前方扇形区域发动重击，造成基础伤害 + 燃烧加成 + 立方体粒子特效。</p>
 *
 * <p>燃烧加成：若目标处于燃烧状态，额外延长 3 秒燃烧并增加 1 点伤害。</p>
 *
 * @see Artifact 法器基类
 */
public class FloatDream extends Artifact {

    // ======================== 常量 ========================

    /** 魔法阵数量 */
    private static final int SQUARE_COUNT = 4;

    /** 法阵半边长 */
    private static final double SQUARE_HALF_SIDE = 0.5;

    /** 立方体粒子半边长 */
    private static final double CUBE_HALF_SIDE = 0.75;

    /** 立方体每条边的粒子密度 */
    private static final int PARTICLES_PER_EDGE = 5;

    /** 魔法阵每边的粒子密度 */
    private static final int PARTICLES_PER_SIDE = 8;

    /** 扇形检测半径 */
    private static final double SEMI_CIRCLE_RADIUS = 5.0;

    /** 魔法阵检测半径 */
    private static final double MAGIC_CIRCLE_RADIUS = 4.0;

    /** 魔法阵检测盒半边长 */
    private static final double BOX_HALF_SIDE = 0.5;

    /** 燃烧延长时间（tick） */
    private static final int BURN_EXTEND_TICKS = 60;

    /** 燃烧额外伤害 */
    private static final float BURN_BONUS_DAMAGE = 1.0F;

    /** 魔法阵基础伤害 */
    private static final float MAGIC_CIRCLE_DAMAGE = 7.0F;

    /** 燃烧粒子数量 */
    private static final int FLAME_PARTICLE_COUNT = 10;

    // ======================== 构造方法 ========================

    /**
     * 千夜浮梦
     * <ul>
     *   <li>基础伤害：8.0</li>
     *   <li>状态效果：Dendro（10 秒）</li>
     *   <li>左键冷却：3 tick（≈0.15 秒）</li>
     *   <li>右键冷却：16 tick（≈0.8 秒）</li>
     *   <li>右键蓄力：14 tick（≈0.7 秒）</li>
     * </ul>
     */
    public FloatDream(Settings settings) {
        super(8.0F, ModEffects.DENDRO, 0, 200, 3, 16, 14, settings);
    }

    // ======================== Tooltip ========================

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext ctx) {
        tooltip.add(Text.translatable("item.kusanali.float_dream.tooltip_1")
                .setStyle(Style.EMPTY.withColor(Formatting.GREEN)));
        tooltip.add(Text.translatable("item.kusanali.float_dream.tooltip_2")
                .setStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE)));
        tooltip.add(Text.translatable("item.kusanali.float_dream.tooltip_3")
                .setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN)));
        tooltip.add(Text.translatable("item.kusanali.float_dream.tooltip_4")
                .setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN)));
    }

    // ======================== 左键攻击 ========================

    /**
     * 左键攻击：向前方生成 {@link #SQUARE_COUNT} 个魔法阵
     *
     * <p>每个魔法阵检测 1×1 范围内的敌人，造成 {@link #MAGIC_CIRCLE_DAMAGE} 点伤害
     * 并附加 10 秒 Dendro 效果。若目标处于燃烧状态，额外触发燃烧加成。</p>
     */
    @Override
    public ActionResult leftClickAction(World world, PlayerEntity player, Hand hand, float chargeProgress) {
        if (!world.isClient) {
            performSpecialAttack(world, player);
        }
        return ActionResult.SUCCESS;
    }

    /**
     * 执行特殊攻击：沿视线方向生成 4 个魔法阵
     */
    private void performSpecialAttack(World world, PlayerEntity player) {
        // 1. 计算 4 个魔法阵的中心位置
        Vec3d[] centers = calculateMagicCircleCenters(player);

        // 2. 生成粒子特效
        for (Vec3d center : centers) {
            spawnSquareParticles(world, center);
        }

        // 3. 检测并伤害敌人
        Set<LivingEntity> affectedEntities = collectAffectedEntities(world, player, centers);
        for (LivingEntity entity : affectedEntities) {
            float damageAmount = MAGIC_CIRCLE_DAMAGE + applyBurnBonus(world, entity);
            entity.damage(ModDamageTypes.dendro(world), damageAmount);
            applyStatusEffect(entity, player);
        }
    }

    /**
     * 计算 4 个魔法阵的中心位置
     *
     * <p>沿玩家视线方向均匀分布，距离分别为 1/4、2/4、3/4、4/4 的 {@link #MAGIC_CIRCLE_RADIUS}。</p>
     */
    private Vec3d[] calculateMagicCircleCenters(PlayerEntity player) {
        Vec3d playerPos = player.getPos();
        Vec3d lookVec = player.getRotationVec(1.0F).multiply(MAGIC_CIRCLE_RADIUS);

        Vec3d[] centers = new Vec3d[SQUARE_COUNT];
        for (int i = 0; i < SQUARE_COUNT; i++) {
            double ratio = (i + 1.0) / SQUARE_COUNT;
            centers[i] = playerPos.add(lookVec.multiply(ratio).add(0.2, 0.1, 0));
        }
        return centers;
    }

    /**
     * 收集魔法阵范围内所有受影响的实体
     */
    private Set<LivingEntity> collectAffectedEntities(World world, PlayerEntity player, Vec3d[] centers) {
        Set<LivingEntity> affectedEntities = new HashSet<>();
        for (Vec3d center : centers) {
            Box detectionBox = new Box(
                    center.add(-BOX_HALF_SIDE, -BOX_HALF_SIDE, -BOX_HALF_SIDE),
                    center.add(BOX_HALF_SIDE, BOX_HALF_SIDE, BOX_HALF_SIDE)
            );
            List<LivingEntity> entitiesInBox = world.getEntitiesByClass(
                    LivingEntity.class,
                    detectionBox,
                    entity -> entity != player && entity.isAlive()
            );
            affectedEntities.addAll(entitiesInBox);
        }
        return affectedEntities;
    }

    // ======================== 右键蓄力攻击 ========================

    /**
     * 右键蓄力攻击：对前方扇形区域发动重击
     *
     * <p>检测前方 {@link #SEMI_CIRCLE_RADIUS} 格半径、180° 扇形范围内的敌人，
     * 对最近的敌人造成基础伤害 + 燃烧加成，并生成立方体粒子特效。</p>
     */
    @Override
    public TypedActionResult<ItemStack> rightClickAction(World world, PlayerEntity player, Hand hand, float chargeProgress) {
        if (!world.isClient) {
            executeHeavyAttack(world, player);
        }
        return TypedActionResult.success(player.getStackInHand(hand));
    }

    /**
     * 执行蓄力重击
     */
    private void executeHeavyAttack(World world, PlayerEntity player) {
        // 1. 检测前方扇形区域
        Optional<LivingEntity> target = findNearestTargetInSemiCircle(world, player);

        // 2. 对最近的目标造成伤害
        target.ifPresent(targetEntity -> {
            float totalDamage = this.attackDamage + applyBurnBonus(world, targetEntity);
            targetEntity.damage(ModDamageTypes.dendro(world), totalDamage);
            applyStatusEffect(targetEntity, player);
            spawnCubeParticles(world, targetEntity);
        });
    }

    /**
     * 在玩家前方扇形区域内查找最近的敌人
     *
     * <p>检测半径为 {@link #SEMI_CIRCLE_RADIUS} 格，角度为 180°（玩家视线正前方）。</p>
     */
    private Optional<LivingEntity> findNearestTargetInSemiCircle(World world, PlayerEntity player) {
        Vec3d playerPos = player.getPos();
        Vec3d lookVec = player.getRotationVec(1.0F);

        // 构建检测包围盒
        Vec3d areaCenter = playerPos.add(lookVec.multiply(SEMI_CIRCLE_RADIUS));
        Box detectionBox = new Box(
                areaCenter.add(-SEMI_CIRCLE_RADIUS, -SEMI_CIRCLE_RADIUS, -SEMI_CIRCLE_RADIUS),
                areaCenter.add(SEMI_CIRCLE_RADIUS, SEMI_CIRCLE_RADIUS, SEMI_CIRCLE_RADIUS)
        );

        // 获取范围内所有存活实体
        List<Entity> entitiesInArea = world.getOtherEntities(
                player,
                detectionBox,
                entity -> entity instanceof LivingEntity living && !living.isSpectator()
        );

        // 筛选扇形区域（dot > 0 表示夹角 < 90°）并按距离排序
        return entitiesInArea.stream()
                .filter(entity -> {
                    Vec3d toEntity = entity.getPos().subtract(playerPos).normalize();
                    return toEntity.dotProduct(lookVec) > 0;
                })
                .min(Comparator.comparingDouble(e -> e.squaredDistanceTo(playerPos)))
                .map(entity -> (LivingEntity) entity);
    }

    // ======================== 辅助逻辑 ========================

    /**
     * 处理燃烧加成逻辑
     *
     * <p>若目标处于燃烧状态：</p>
     * <ul>
     *   <li>延长燃烧时间 {@link #BURN_EXTEND_TICKS} tick（3 秒）</li>
     *   <li>生成火焰粒子特效</li>
     *   <li>播放燃烧音效</li>
     *   <li>返回额外伤害值 {@link #BURN_BONUS_DAMAGE}</li>
     * </ul>
     *
     * @return 额外伤害值（未燃烧返回 0）
     */
    private float applyBurnBonus(World world, LivingEntity target) {
        if (!target.isOnFire()) {
            return 0F;
        }

        // 延长燃烧时间
        target.setFireTicks(target.getFireTicks() + BURN_EXTEND_TICKS);

        // 服务端生成火焰粒子与音效
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(
                    ParticleTypes.FLAME,
                    target.getX(),
                    target.getY() + 1,
                    target.getZ(),
                    FLAME_PARTICLE_COUNT,
                    0.5, 0.5, 0.5,
                    0.05
            );
            world.playSound(
                    null,
                    target.getBlockPos(),
                    SoundEvents.ENTITY_BLAZE_BURN,
                    SoundCategory.NEUTRAL,
                    0.5F,
                    1.0F
            );
        }

        return BURN_BONUS_DAMAGE;
    }

    /**
     * 生成立方体粒子特效（围绕目标）
     *
     * <p>在目标周围生成一个边长为 {@link #CUBE_HALF_SIDE}×2 的立方体轮廓，
     * 每条边有 {@link #PARTICLES_PER_EDGE} 个粒子。</p>
     */
    private void spawnCubeParticles(World world, LivingEntity target) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }

        double cx = target.getX();
        double cy = target.getY() + target.getHeight() / 2;
        double cz = target.getZ();
        double hs = CUBE_HALF_SIDE;

        // 立方体 8 个顶点
        double[][] vertices = {
                {cx - hs, cy - hs, cz - hs}, // 0: 左下前
                {cx + hs, cy - hs, cz - hs}, // 1: 右下前
                {cx + hs, cy - hs, cz + hs}, // 2: 右后前
                {cx - hs, cy - hs, cz + hs}, // 3: 左后前
                {cx - hs, cy + hs, cz - hs}, // 4: 左上后
                {cx + hs, cy + hs, cz - hs}, // 5: 右上后
                {cx + hs, cy + hs, cz + hs}, // 6: 右后后
                {cx - hs, cy + hs, cz + hs}  // 7: 左后后
        };

        // 立方体 12 条棱
        int[][] edges = {
                {0, 1}, {1, 2}, {2, 3}, {3, 0}, // 底面
                {4, 5}, {5, 6}, {6, 7}, {7, 4}, // 顶面
                {0, 4}, {1, 5}, {2, 6}, {3, 7}  // 侧面
        };

        // 沿每条边生成粒子
        for (int[] edge : edges) {
            double[] start = vertices[edge[0]];
            double[] end = vertices[edge[1]];

            for (int i = 0; i <= PARTICLES_PER_EDGE; i++) {
                double ratio = (double) i / PARTICLES_PER_EDGE;
                double px = start[0] + ratio * (end[0] - start[0]);
                double py = start[1] + ratio * (end[1] - start[1]);
                double pz = start[2] + ratio * (end[2] - start[2]);

                serverWorld.spawnParticles(
                        ParticleTypes.HAPPY_VILLAGER,
                        px, py, pz,
                        1,       // 粒子数量
                        0, 0, 0, // 扩散范围
                        0        // 速度
                );
            }
        }
    }

    /**
     * 生成正方形魔法阵粒子
     *
     * <p>在指定中心位置生成一个边长为 {@link #SQUARE_HALF_SIDE}×2 的正方形轮廓，
     * 每条边有 {@link #PARTICLES_PER_SIDE} 个粒子，中心额外生成粒子。</p>
     */
    private static void spawnSquareParticles(World world, Vec3d center) {
        double hs = SQUARE_HALF_SIDE;

        Vec3d[] corners = {
                new Vec3d(center.x - hs, center.y, center.z - hs),
                new Vec3d(center.x + hs, center.y, center.z - hs),
                new Vec3d(center.x + hs, center.y, center.z + hs),
                new Vec3d(center.x - hs, center.y, center.z + hs)
        };

        if (world.isClient) {
            for (int i = 0; i < 4; i++) {
                Vec3d start = corners[i];
                Vec3d end = corners[(i + 1) % 4];
                for (int j = 0; j <= PARTICLES_PER_SIDE; j++) {
                    double ratio = (double) j / PARTICLES_PER_SIDE;
                    double px = start.x + ratio * (end.x - start.x);
                    double pz = start.z + ratio * (end.z - start.z);
                    world.addParticle(ParticleTypes.HAPPY_VILLAGER, px, center.y, pz, 0, 0, 0);
                }
            }
            world.addParticle(ParticleTypes.HAPPY_VILLAGER, center.x, center.y, center.z, 0, 0, 0);
        } else {
            ServerWorld serverWorld = (ServerWorld) world;
            for (int i = 0; i < 4; i++) {
                Vec3d start = corners[i];
                Vec3d end = corners[(i + 1) % 4];
                for (int j = 0; j <= PARTICLES_PER_SIDE; j++) {
                    double ratio = (double) j / PARTICLES_PER_SIDE;
                    double px = start.x + ratio * (end.x - start.x);
                    double pz = start.z + ratio * (end.z - start.z);
                    serverWorld.spawnParticles(ParticleTypes.HAPPY_VILLAGER, px, center.y, pz, 1, 0, 0, 0, 0);
                }
            }
            serverWorld.spawnParticles(ParticleTypes.HAPPY_VILLAGER, center.x, center.y, center.z, 3, 0, 0, 0, 0);
        }
    }

    // ======================== 基础行为 ========================

    /** 使用动作：弓的拉弓动画 */
    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    /** 可用兰那罗之花修复 */
    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.getItem() == ModItems.EMERALD_GEMSTONE;
    }

    /** 允许附魔 */
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    /** 附魔权重 */
    @Override
    public int getEnchantability() {
        return 25;
    }
}