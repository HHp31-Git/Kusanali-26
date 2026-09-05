package com.kusanali.define.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public abstract class TerritoryBossEntity extends TerritoryEntity {

    protected final ServerBossBar bossBar;
    protected TextColor barColor = TextColor.fromFormatting(Formatting.RED);

    private double aggroRange = 24.0;
    private boolean nearDeathTriggered = false;
    private boolean isFinalForm = false;

    protected TerritoryBossEntity(EntityType<? extends TerritoryEntity> entityType, World world) {
        super(entityType, world);

        this.bossBar = new ServerBossBar(
                this.getDisplayName(),
                BossBar.Color.RED,
                BossBar.Style.PROGRESS
        );
    }

    // ========== 属性配置 ==========

    public static DefaultAttributeContainer.Builder createBossAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 300.0)
                .add(EntityAttributes.GENERIC_ARMOR, 10.0)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 5.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.28)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 12.0);
    }

    // ========== 血条颜色设置 ==========

    public void setBarColor(TextColor color) {
        this.barColor = color;
        updateBossBarColor();
    }

    public void setBarColor(Formatting formatting) {
        this.barColor = TextColor.fromFormatting(formatting);
        updateBossBarColor();
    }

    private void updateBossBarColor() {
        BossBar.Color mcColor = BossBar.Color.WHITE;
        if (barColor != null) {
            String hex = barColor.getHexCode().toLowerCase();
            mcColor = switch (hex) {
                case "#ff5555", "#aa0000" -> BossBar.Color.RED;
                case "#55ffff", "#00aaaa" -> BossBar.Color.BLUE;
                case "#55ff55", "#00aa00" -> BossBar.Color.GREEN;
                case "#ffff55", "#aaaa00" -> BossBar.Color.YELLOW;
                case "#ff55ff", "#aa00aa" -> BossBar.Color.PURPLE;
                default -> BossBar.Color.WHITE;
            };
        }
        this.bossBar.setColor(mcColor);
    }

    // ========== 仇恨系统 ==========

    @Override
    public boolean canTarget(LivingEntity target) {
        return target instanceof PlayerEntity || target instanceof VillagerEntity;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new ReturnToTerritoryGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 16.0f));
        this.goalSelector.add(4, new LookAtEntityGoal(this, VillagerEntity.class, 16.0f));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, VillagerEntity.class, true));
    }

    // ========== 仇恨范围检测 ==========

    public void setAggroRange(double range) {
        this.aggroRange = range;
    }

    public double getAggroRange() {
        return aggroRange;
    }

    protected void checkForAggro() {
        if (this.getTarget() != null && this.getTarget().isAlive()) {
            return;
        }

        // 使用 getWorld() 替代 world
        Box searchBox = this.getBoundingBox().expand(aggroRange);
        List<PlayerEntity> players = this.getWorld().getNonSpectatingEntities(PlayerEntity.class, searchBox);
        List<VillagerEntity> villagers = this.getWorld().getNonSpectatingEntities(VillagerEntity.class, searchBox);

        if (!players.isEmpty()) {
            this.setTarget(players.get(0));
        } else if (!villagers.isEmpty()) {
            this.setTarget(villagers.get(0));
        }
    }

    // ========== Tick 逻辑 ==========

    @Override
    public void tickMovement() {
        super.tickMovement();

        if (this.getWorld().isClient) return;

        // 更新 Boss 血条
        this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
        this.bossBar.setVisible(!this.isDead());

        // 检测仇恨
        if (this.getTarget() == null || !this.getTarget().isAlive()) {
            checkForAggro();
        }

        // 无仇恨时站桩
        if (this.getTarget() == null || !this.getTarget().isAlive()) {
            this.getNavigation().stop();
            this.setVelocity(0, this.getVelocity().y, 0);
        }

        // 更新血条名称
        this.bossBar.setName(
                Text.literal(this.getDisplayName().getString())
                        .append(" ")
                        .append(String.format("%.0f/%.0f", this.getHealth(), this.getMaxHealth()))
        );
    }

    // ========== 致命伤检测 ==========

    @Override
    public void onDeath(DamageSource damageSource) {
        if (!nearDeathTriggered) {
            nearDeathTriggered = true;

            boolean shouldDie = onNearDeathCheck(damageSource);

            if (!shouldDie) {
                this.setHealth(this.getMaxHealth() * 0.3f);
                this.nearDeathTriggered = false;
                this.setTarget(null);
                onNearDeathRecover();
                return;
            }
        }

        super.onDeath(damageSource);
        this.bossBar.clearPlayers();
    }

    protected boolean onNearDeathCheck(DamageSource source) {
        return nearDeathTriggered;
    }

    protected void onNearDeathRecover() {
        this.playSound(
                net.minecraft.sound.SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE,
                1.0f, 0.5f
        );
    }

    // ========== Boss 血条管理 ==========

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        this.bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
    }

    // ========== 最终形态设置 ==========

    public void setFinalForm(boolean finalForm) {
        this.isFinalForm = finalForm;
    }

    public boolean isFinalForm() {
        return isFinalForm;
    }

    // ========== NBT 持久化 ==========

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putDouble("AggroRange", aggroRange);
        nbt.putBoolean("NearDeathTriggered", nearDeathTriggered);
        nbt.putBoolean("IsFinalForm", isFinalForm);
        if (barColor != null) {
            nbt.putString("BarColor", barColor.getHexCode());
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("AggroRange")) {
            this.aggroRange = nbt.getDouble("AggroRange");
        }
        if (nbt.contains("NearDeathTriggered")) {
            this.nearDeathTriggered = nbt.getBoolean("NearDeathTriggered");
        }
        if (nbt.contains("IsFinalForm")) {
            this.isFinalForm = nbt.getBoolean("IsFinalForm");
        }
        if (nbt.contains("BarColor")) {
            this.barColor = TextColor.parse(nbt.getString("BarColor"));
            updateBossBarColor();
        }
    }
}
