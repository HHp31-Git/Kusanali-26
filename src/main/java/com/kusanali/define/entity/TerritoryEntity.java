package com.kusanali.define.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.EnumSet;

/**
 * 领地生物基类
 * 以某三维位置为中心，超出最大距离时自动返回，返回后逐渐恢复生命值
 */
public abstract class TerritoryEntity extends PathAwareEntity {

    private BlockPos territoryCenter;
    private double maxDistanceFromCenter = 22.0;
    private float healRatePerSecond = 0.2f;
    public boolean returningHome = false;
    private int ticksSinceReturned = 0;

    protected TerritoryEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        // 默认以出生位置为领地中心
        this.territoryCenter = this.getBlockPos();
    }

    // ========== 领地配置方法 ==========

    public void setTerritoryCenter(BlockPos center) {
        this.territoryCenter = center;
    }

    public BlockPos getTerritoryCenter() {
        return territoryCenter;
    }

    public void setMaxDistanceFromCenter(double distance) {
        this.maxDistanceFromCenter = distance;
    }

    public double getMaxDistanceFromCenter() {
        return maxDistanceFromCenter;
    }

    public void setHealRatePerSecond(float rate) {
        this.healRatePerSecond = rate;
    }

    public float getHealRatePerSecond() {
        return healRatePerSecond;
    }

    public boolean isReturningHome() {
        return returningHome;
    }

    // ========== 距离检查 ==========

    public boolean isWithinTerritory() {
        return squaredDistanceToCenter() <= maxDistanceFromCenter * maxDistanceFromCenter;
    }

    public double squaredDistanceToCenter() {
        double dx = this.getX() - (territoryCenter.getX() + 0.5);
        double dy = this.getY() - territoryCenter.getY();
        double dz = this.getZ() - (territoryCenter.getZ() + 0.5);
        return dx * dx + dy * dy + dz * dz;
    }

    public double distanceToCenter() {
        return Math.sqrt(squaredDistanceToCenter());
    }

    // ========== 返回行为 ==========

    public void startReturningHome() {
        this.returningHome = true;
        this.ticksSinceReturned = 0;
    }

    public void finishReturningHome() {
        this.returningHome = false;
        this.ticksSinceReturned = 0;
    }

    // ========== NBT 持久化 ==========

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putIntArray("TerritoryCenter", new int[]{
                territoryCenter.getX(),
                territoryCenter.getY(),
                territoryCenter.getZ()
        });
        nbt.putDouble("MaxDistanceFromCenter", maxDistanceFromCenter);
        nbt.putFloat("HealRatePerSecond", healRatePerSecond);
        nbt.putBoolean("ReturningHome", returningHome);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("TerritoryCenter")) {
            int[] center = nbt.getIntArray("TerritoryCenter");
            this.territoryCenter = new BlockPos(center[0], center[1], center[2]);
        }
        if (nbt.contains("MaxDistanceFromCenter")) {
            this.maxDistanceFromCenter = nbt.getDouble("MaxDistanceFromCenter");
        }
        if (nbt.contains("HealRatePerSecond")) {
            this.healRatePerSecond = nbt.getFloat("HealRatePerSecond");
        }
        if (nbt.contains("ReturningHome")) {
            this.returningHome = nbt.getBoolean("ReturningHome");
        }
    }

    // ========== Tick 逻辑（生命恢复） ==========

    @Override
    public void tickMovement() {
        super.tickMovement();

        if (this.getWorld().isClient) return;

        // 如果正在返回，不处理恢复
        if (returningHome) return;

        // 检查是否在领地内
        if (!isWithinTerritory()) {
            // 超出领地，开始返回
            startReturningHome();
            return;
        }

        // 在领地内，检查是否需要恢复生命
        if (distanceToCenter() <= 1.0) {
            // 在中心附近，开始恢复
            ticksSinceReturned++;

            // 每秒恢复（20 tick = 1 秒）
            if (ticksSinceReturned % 20 == 0) {
                float maxHealth = this.getMaxHealth();
                float healAmount = maxHealth * healRatePerSecond;
                this.heal(healAmount);
            }
        } else {
            // 不在中心附近，重置计时器
            ticksSinceReturned = 0;
        }
    }

    // ========== 返回 Goal（内部类） ==========

    /**
     * 返回领地的 Goal，优先级最高
     */
    public static class ReturnToTerritoryGoal extends Goal {

        private final TerritoryEntity entity;
        private Path path;
        private int cooldown = 0;

        public ReturnToTerritoryGoal(TerritoryEntity entity) {
            this.entity = entity;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            // 只有在需要返回时启动
            if (!entity.returningHome) return false;
            if (cooldown > 0) {
                cooldown--;
                return false;
            }
            return true;
        }

        @Override
        public boolean shouldContinue() {
            // 一直执行直到返回完成
            return entity.returningHome;
        }

        @Override
        public void start() {
            // 计算路径到领地中心
            BlockPos target = entity.territoryCenter;
            this.path = entity.getNavigation().findPathTo(target, 0);
            if (this.path != null) {
                entity.getNavigation().startMovingAlong(this.path, 1.0);
            } else {
                // 路径计算失败，直接移动
                entity.getMoveControl().moveTo(
                        target.getX() + 0.5,
                        target.getY(),
                        target.getZ() + 0.5,
                        1.0
                );
            }
        }

        @Override
        public void tick() {
            // 每 tick 重新规划路径
            if (entity.age % 20 == 0) { // 每秒重新规划一次
                BlockPos target = entity.territoryCenter;
                this.path = entity.getNavigation().findPathTo(target, 0);
                if (this.path != null) {
                    entity.getNavigation().startMovingAlong(this.path, 1.0);
                }
            }

            // 检查是否到达
            if (entity.distanceToCenter() <= 1.5) {
                // 到达领地中心附近
                entity.finishReturningHome();

                // 播放完成音效（可选）
                entity.playSound(
                        net.minecraft.sound.SoundEvents.ENTITY_ITEM_PICKUP,
                        0.5f, 1.0f
                );
            }

            // 面向中心
            Vec3d centerVec = new Vec3d(
                    entity.territoryCenter.getX() + 0.5,
                    entity.territoryCenter.getY(),
                    entity.territoryCenter.getZ() + 0.5
            );
            entity.getLookControl().lookAt(centerVec);
        }

        @Override
        public void stop() {
            entity.getNavigation().stop();
            cooldown = 100; // 5 秒冷却，防止频繁触发
        }
    }
}

