package com.kusanali.entity.custom;

import com.kusanali.entity.ModEntities;
import com.kusanali.entity.tracker.SeedEffectTracker;
import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class DendroSeedEntity extends LivingEntity {
    // 最大寿命
    private static final int MAX_LIFETIME = 80;
    // 创建时间
    private long creationTime;

    /** 弹射物生成冷却 */
    private static final int PROJECTILE_COOLDOWN = 60;

    /** 爆炸半径 */
    private static final double EXPLOSION_RADIUS = 5.0;

    /** 基础伤害 */
    private static final float BASE_DAMAGE = 2.0f;

    /** 玩家伤害 */
    private static final float PLAYER_DAMAGE = 1.0f;

    /** Dendro 效果持续时间（10 秒 = 200 tick） */
    private static final int DENDRO_DURATION = 200;

    /** Pyro 效果持续时间（10 秒 = 200 tick） */
    private static final int PYRO_DURATION = 200;

    private boolean initialized = false;
    private long lastProjectileTime = -1;

    /** 弹射物纹理变体 */
    private String textureVariant = "default";

    public DendroSeedEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
        this.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING,
                -1, 0, false, false, true));
        this.setInvulnerable(true);
    }
    public static DefaultAttributeContainer.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, Float.MAX_VALUE)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 0.0D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.0D);
    }
    @Override
    public boolean damage(DamageSource source, float amount) {
        return !this.getWorld().isClient;
    }

    @Override
    public void kill() {
        this.setHealth(this.getMaxHealth());
    }

    @Override
    public void setHealth(float health) {
        super.setHealth(Float.MAX_VALUE);
    }

    @Override
    public void tick() {
        super.tick();
        if (!initialized) {
            initialized = true;
            this.creationTime = this.getWorld().getTime();
            this.lastProjectileTime = this.creationTime;
        }
        super.tick();
        // 锁定位置：原地不动
        this.setVelocity(0, 0, 0);
        this.setNoGravity(true);
        this.prevX = this.getX();
        this.prevY = this.getY();
        this.prevZ = this.getZ();

        if (this.getWorld().isClient()) return;

        ServerWorld serverWorld = (ServerWorld) this.getWorld();
        SeedEffectTracker.tickAll(serverWorld);

        // 检查寿命
        long age = serverWorld.getTime() - creationTime;
        if (age >= MAX_LIFETIME) {
            // 到达寿命，触发爆炸
            explodeWithEffect(serverWorld, ModEffects.DENDRO, DENDRO_DURATION, ParticleTypes.HAPPY_VILLAGER);
            return;
        }

        // 每 6 秒执行一次逻辑
        if (serverWorld.getTime() - lastProjectileTime >= PROJECTILE_COOLDOWN) {
            lastProjectileTime = serverWorld.getTime();
            executeLogic();
        }
    }

    @Override
    public Arm getMainArm() {
        return Arm.RIGHT;
    }

    private boolean shouldExplode() {
        return SeedEffectTracker.hasEffect(this.getUuid(), ModEffects.PYRO);
    }

    private void explodeWithEffect(ServerWorld world) {
        if (shouldExplode()) {
            explodeWithEffect(world, ModEffects.PYRO, PYRO_DURATION, ParticleTypes.FLAME);
        } else {
            explodeWithEffect(world, ModEffects.DENDRO, DENDRO_DURATION, ParticleTypes.HAPPY_VILLAGER);
        }
    }

    /**
     * 核心逻辑：根据状态效果执行不同的行为
     */
    private void executeLogic() {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            if (shouldExplode()) {
                explodeWithEffect(serverWorld);
            }
            if (this.hasStatusEffect(ModEffects.ELECTRO)){
                spawnProjectileAndRemove(serverWorld);
            }
            this.discard();
        }
    }

    /**
     * 爆炸并赋予效果
     */
    private void explodeWithEffect(ServerWorld world, net.minecraft.entity.effect.StatusEffect effect,
                                   int duration, net.minecraft.particle.ParticleEffect particle) {
        Vec3d center = this.getPos();
        Box searchBox = new Box(
                center.x - EXPLOSION_RADIUS, center.y - EXPLOSION_RADIUS, center.z - EXPLOSION_RADIUS,
                center.x + EXPLOSION_RADIUS, center.y + EXPLOSION_RADIUS, center.z + EXPLOSION_RADIUS
        );

        List<LivingEntity> targets = world.getEntitiesByClass(
                LivingEntity.class,
                searchBox,
                e -> e.isAlive() && !e.getUuid().equals(this.getUuid())
        );

        for (LivingEntity target : targets) {
            double distance = target.squaredDistanceTo(center);
            if (distance > EXPLOSION_RADIUS * EXPLOSION_RADIUS) continue;

            // 玩家始终 1 点伤害，其他生物 2 点
            float damage = (target instanceof PlayerEntity) ? PLAYER_DAMAGE : BASE_DAMAGE;
            target.damage(ModDamageTypes.reaction_type_2(world), damage);

            // 赋予效果
            target.addStatusEffect(new StatusEffectInstance(
                    effect,
                    duration,
                    0,
                    false,
                    true,
                    true
            ));
        }

        // 粒子特效
        spawnExplosionParticles(world, center, particle);

        // 清理效果数据并消失
        SeedEffectTracker.removeSeed(this.getUuid());
        this.discard();
    }

    /**
     * 生成弹射物并消失
     */
    private void spawnProjectileAndRemove(ServerWorld world) {
        SeedProjectileEntity projectile = ModEntities.SEED_PROJECTILE.create(world);
        if (projectile != null) {
            projectile.setPosition(this.getX(), this.getY(), this.getZ());
            projectile.setVelocity(0, 0.125, 0);
            world.spawnEntity(projectile);
        }

        // 消失粒子
        Vec3d pos = this.getPos();
        world.spawnParticles(
                ParticleTypes.ELECTRIC_SPARK,
                pos.x, pos.y + 0.5, pos.z,
                10, 0.3, 0.3, 0.3, 0.1
        );

        SeedEffectTracker.removeSeed(this.getUuid());
        this.discard();
    }

    /**
     * 生成爆炸粒子特效
     */
    private void spawnExplosionParticles(ServerWorld world, Vec3d center, net.minecraft.particle.ParticleEffect particle) {
        // 径向粒子爆发
        for (int i = 0; i < 30; i++) {
            double angle = world.random.nextDouble() * Math.PI * 2;
            double radius = world.random.nextDouble() * EXPLOSION_RADIUS;
            double height = (world.random.nextDouble() - 0.5) * EXPLOSION_RADIUS;

            double px = center.x + Math.cos(angle) * radius;
            double py = center.y + 0.5 + height;
            double pz = center.z + Math.sin(angle) * radius;

            world.spawnParticles(particle, px, py, pz, 1,
                    Math.cos(angle) * 0.05,
                    world.random.nextDouble() * 0.05,
                    Math.sin(angle) * 0.05,
                    0.05);
        }

        // 同心圆扩散环
        for (int ring = 0; ring < 3; ring++) {
            double ringRadius = 1.0 + ring * 1.5;
            for (int i = 0; i < 12; i++) {
                double angle = (Math.PI * 2 / 12) * i;
                world.spawnParticles(ParticleTypes.CLOUD,
                        center.x + Math.cos(angle) * ringRadius,
                        center.y + 0.3,
                        center.z + Math.sin(angle) * ringRadius,
                        1, 0, 0.05, 0, 0.03);
            }
        }

        // 中心闪光
        world.spawnParticles(ParticleTypes.CRIT, center.x, center.y + 0.5, center.z, 8, 0.2, 0.2, 0.2, 0.1);
    }

    /* ======================== NBT 持久化 ======================== */
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("TextureVariant")) {
            this.textureVariant = nbt.getString("TextureVariant");
        }
        if (nbt.contains("LastProjectileTime")) {
            this.lastProjectileTime = nbt.getLong("LastProjectileTime");
        }
    }
    @Override
    public boolean isImmuneToExplosion() {
        return true;
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return List.of();
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {

    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putString("TextureVariant", textureVariant);
        nbt.putLong("LastProjectileTime", lastProjectileTime);
    }
}