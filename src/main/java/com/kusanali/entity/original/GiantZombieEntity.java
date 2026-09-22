package com.kusanali.entity.original;

import com.kusanali.register.ModGameRules;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class GiantZombieEntity extends ZombieEntity {
    private int fireHits = 0;
    private long dashCooldownEnd = 0;

    public GiantZombieEntity(EntityType<? extends ZombieEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return ZombieEntity.createZombieAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 80.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.3);
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return EntityDimensions.fixed(1.4F, 3.7F);
    }

    @Override
    public float getEyeHeight(EntityPose pose) {
        return 3.35F;
    }

    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
        ItemStack shield = new ItemStack(Items.SHIELD);
        this.equipStack(EquipmentSlot.OFFHAND, shield);
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
    }

    /* ---------- 火焰伤害计数 ---------- */
    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.isOf(DamageTypes.ON_FIRE)
                || source.isOf(DamageTypes.IN_FIRE)
                || source.isOf(DamageTypes.FIREBALL)
                || source.isOf(DamageTypes.LAVA)
                ||source.isOf(DamageTypes.UNATTRIBUTED_FIREBALL)) {
            fireHits++;
            if (fireHits >= 2 && !this.getOffHandStack().isEmpty()) {
                this.equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            }
        }

        if (isShieldActive() && isFrontalAttack(source)) {
            amount *= 0.4F;
        }

        return super.damage(source, amount);
    }

    private boolean isFrontalAttack(DamageSource source) {
        Entity attacker = source.getAttacker();
        if (attacker == null) return false;
        Vec3d toAttacker = attacker.getPos().subtract(this.getPos()).normalize();
        Vec3d facing = this.getRotationVector();
        return toAttacker.dotProduct(facing) > 0.0;
    }

    public boolean isShieldActive() {
        ItemStack off = this.getOffHandStack();
        return !off.isEmpty() && off.getItem() == Items.SHIELD;
    }

    //冲刺技能
    public void tryDash() {
        long time = this.getWorld().getTime();
        if (time < dashCooldownEnd) return;

        this.addStatusEffect(new StatusEffectInstance(
                StatusEffects.SPEED,
                60,
                2,
                false, false, true
        ));

        dashCooldownEnd = time + 400;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient) return;

        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive()
                && this.distanceTo(target) < 9
                && this.getWorld().getTime() >= dashCooldownEnd) {
            this.tryDash();
        }
    }
    public static boolean canNaturallySpawn(ServerWorldAccess world,
                                            BlockPos pos,
                                            Random random) {
        if (!(world instanceof ServerWorld sw)) return false;
        if (!sw.getGameRules().getBoolean(ModGameRules.REINFORCE)) {
            return false;
        }
        int y = pos.getY();
        if (y < 30 || y > 150) return false;
        return HostileEntity.isSpawnDark(world, pos, random)
                && world.getBlockState(pos.down()).isSolidBlock(world, pos.down());
    }
}
