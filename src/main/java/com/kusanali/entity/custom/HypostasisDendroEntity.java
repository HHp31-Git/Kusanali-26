package com.kusanali.entity.custom;

import com.kusanali.define.entity.TerritoryBossEntity;
import com.kusanali.define.entity.TerritoryEntity;
import com.kusanali.entity.structure_control.HypostasisSpawnState;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Objects;

public class HypostasisDendroEntity extends TerritoryBossEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int healTickCounter = 0;
    private boolean spawnChecked = false;

    public HypostasisDendroEntity(EntityType<? extends TerritoryEntity> entityType, World world) {
        super(entityType, world);

        this.setBarColor(Formatting.GREEN);
        this.setAggroRange(16.0);
        this.setMaxDistanceFromCenter(21.0);
        this.setHealRatePerSecond(0.20f);
    }


    public static DefaultAttributeContainer.Builder createHypostasisAttributes() {
        return TerritoryBossEntity.createBossAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 100.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23 * 0.8)
                .add(EntityAttributes.GENERIC_ARMOR, 0.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
                                 SpawnReason spawnReason, @Nullable EntityData entityData,
                                 @Nullable NbtCompound nbt) {
        entityData = super.initialize(world, difficulty, spawnReason, entityData, nbt);
        this.setHealth(this.getMaxHealth());
        return entityData;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();

        if (this.getWorld().isClient) return;

        if (!spawnChecked) {
            spawnChecked = true;
            if (!isAtAltarSpawnPoint() || hasBossAlreadySpawned()) {
                this.discard();
                return;
            }
            markBossSpawned();
        }

        LivingEntity target = this.getTarget();
        boolean hasTarget = (target != null && target.isAlive());

        if (hasTarget || this.returningHome) {
            healTickCounter = 0;
        } else {
            healTickCounter++;
            if (healTickCounter >= 20) {
                healTickCounter = 0;
                this.heal(this.getMaxHealth() * 0.20f);
            }
        }

        Text firstLine = Text.literal("无相之草");
        Text secondLine = Text.literal("§7" + "扎因" + "§r");

        this.bossBar.setName(
                Text.literal("")
                        .append(firstLine)
                        .append("\n")
                        .append(secondLine)
        );
    }

    @Override
    protected boolean onNearDeathCheck(DamageSource source) {
        return true;
    }

    // ===== 结构绑定生成 =====

    /**
     * 获取 kusanali:altar 结构的唯一中心方块坐标
     */
    private BlockPos getAltarCenterPos() {
        if (this.getWorld().isClient) return null;

        ServerWorld world = (ServerWorld) this.getWorld();
        RegistryKey<Structure> altarKey =
                RegistryKey.of(RegistryKeys.STRUCTURE, new Identifier("kusanali", "altar"));

        StructureStart start = world.getStructureAccessor()
                .getStructureContaining(this.getBlockPos(), altarKey);

        if (start == null || !start.hasChildren()) return null;

        BlockBox box = start.getBoundingBox();

        // 长方体中心（取 XZ 中心，Y 取最下层）
        int cx = (box.getMinX() + box.getMaxX()) / 2;   // X 中心
        int cz = (box.getMinZ() + box.getMaxZ()) / 2;   // Z 中心
        int cy = box.getMinY();                           // Y 取最下层（base）

        return new BlockPos(cx, cy, cz);
    }

    /**
     * 判断当前实体是否在 altar 中心方块的正上方 1 格
     */
    private boolean isAtAltarSpawnPoint() {
        BlockPos center = getAltarCenterPos();
        if (center == null) return false;

        // 生物脚部位置 = center.up()（结构空位所在格）
        BlockPos spawnPos = center.up();

        // 允许误差：生物碰撞箱中心可能在 center.up() 或 center.up().up()
        return this.getBlockPos().equals(spawnPos) ||
                this.getBlockPos().equals(spawnPos.up());
    }

    /**
     * 检查该结构是否已经生成过此 Boss
     */
    private boolean hasBossAlreadySpawned() {
        HypostasisSpawnState state = HypostasisSpawnState.getServerState(Objects.requireNonNull(this.getWorld().getServer()));
        String structureKey = getCurrentStructureId();
        return state.hasSpawned(structureKey);
    }

    /**
     * 获取当前所在结构的唯一标识（以中心坐标为 ID）
     */
    private String getCurrentStructureId() {
        BlockPos center = getAltarCenterPos();
        if (center == null) return "unknown";
        return "kusanali:altar@" + center.getX() + "_" + center.getY() + "_" + center.getZ();
    }

    /**
     * 标记该结构已生成 Boss
     */
    private void markBossSpawned() {
        HypostasisSpawnState state = HypostasisSpawnState.getServerState(Objects.requireNonNull(this.getWorld().getServer()));
        state.markSpawned(getCurrentStructureId());
    }

    // ===== GeckoLib 动画 =====

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this,
                "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<HypostasisDendroEntity> state) {
        LivingEntity target = this.getTarget();
        boolean hasTarget = (target != null && target.isAlive());

        if (hasTarget) {
            state.setAnimation(RawAnimation.begin().thenPlay("attack"));
        } else if (state.getLimbSwingAmount() > 0.01f) {
            state.setAnimation(RawAnimation.begin().thenPlay("walk"));
        } else {
            state.setAnimation(RawAnimation.begin().thenPlay("idle"));
        }

        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
