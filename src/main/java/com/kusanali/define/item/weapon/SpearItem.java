package com.kusanali.define.item.weapon;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public abstract class SpearItem extends ToolItem implements Vanishable {
    private static final UUID ATTACK_DAMAGE_MODIFIER_ID = UUID.
            fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    private static final UUID ATTACK_SPEED_MODIFIER_ID = UUID.
            fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");

    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;
    private final int attackDamage;
    private final AttackMode mode;

    public enum AttackMode {
        HEAVY_STRIKE,
        CHARGE,
        NONE
    }

    public SpearItem(ToolMaterial material, int attackDamage, float attackSpeed, AttackMode mode, Settings settings) {
        super(material, settings);
        this.attackDamage = attackDamage;
        this.mode = mode;

        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();

        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier",
                        this.attackDamage, EntityAttributeModifier.Operation.ADDITION));

        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED,
                new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier",
                        attackSpeed, EntityAttributeModifier.Operation.ADDITION));

        this.attributeModifiers = builder.build();
    }

    public AttackMode getMode() {
        return mode;
    }

    public int getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public boolean canMine(BlockState state, World world, net.minecraft.util.math.BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        if (state.isOf(Blocks.COBWEB)) {
            return 15.0F;
        }
        return 1.0F;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, (e) -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state,
                            net.minecraft.util.math.BlockPos pos, LivingEntity miner) {
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

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (user.getItemCooldownManager().isCoolingDown(this)) {
            return TypedActionResult.fail(stack);
        }

        if (!world.isClient) {
            switch (mode) {
                case HEAVY_STRIKE:
                    performHeavyStrike(world, user, stack);
                    break;
                case CHARGE:
                    performCharge(world, user, stack);
                    break;
                case NONE:
                    break;
            }
        }

        return TypedActionResult.success(stack);
    }

    private void performHeavyStrike(World world, PlayerEntity player, ItemStack stack) {
        float sweepDamage = this.attackDamage - 2;
        performSweepAttack(world, player, sweepDamage);

        scheduleTask(() -> {
            double reachDistance = getReachDistance(player) - 0.5;
            Entity target = getFirstEntityInLine(player, reachDistance);

            if (target instanceof LivingEntity livingTarget) {
                livingTarget.damage(player.getDamageSources().playerAttack(player), this.attackDamage);

                // 上挑效果
                Vec3d upwardMotion = new Vec3d(0, 0.5, 0);
                livingTarget.setVelocity(livingTarget.getVelocity().add(upwardMotion));
                livingTarget.velocityModified = true;

                // 击退1
                Vec3d knockbackDir = new Vec3d(
                        player.getX() - livingTarget.getX(),
                        0,
                        player.getZ() - livingTarget.getZ()
                ).normalize();
                livingTarget.takeKnockback(1.0, knockbackDir.x, knockbackDir.z);

                world.playSound(null, livingTarget.getX(), livingTarget.getY(), livingTarget.getZ(),
                        SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS, 1.0F, 1.0F);

                spawnUpwardParticles(world, livingTarget.getPos());
            }

            player.getItemCooldownManager().set(this, 20);
        }, 10);
    }

    private void performCharge(World world, PlayerEntity player, ItemStack stack) {
        float sweepDamage = this.attackDamage - 2;
        performSweepAttack(world, player, sweepDamage);

        scheduleTask(() -> {
            Vec3d lookVec = player.getRotationVec(1.0F);

            Vec3d motion = lookVec.multiply(0.5);
            player.setVelocity(motion.x, motion.y * 0.3, motion.z);
            player.velocityModified = true;

            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ITEM_TRIDENT_RIPTIDE_3, SoundCategory.PLAYERS, 1.0F, 1.0F);

            spawnChargeParticles(world, player);

            scheduleTask(() -> {
                Box detectionBox = new Box(
                        player.getX() - 2, player.getY() - 1, player.getZ() - 2,
                        player.getX() + 2, player.getY() + 1, player.getZ() + 2
                );

                List<LivingEntity> pathEntities = world.getNonSpectatingEntities(
                        LivingEntity.class, detectionBox);

                for (LivingEntity entity : pathEntities) {
                    if (entity != player && entity.distanceTo(player) <= 2.5f) {
                        float chargeDamage = this.attackDamage - 1;

                        int impalingLevel = EnchantmentHelper.getLevel(Enchantments.IMPALING, stack);
                        if (impalingLevel > 0) {
                            chargeDamage += impalingLevel * 1.25f;
                        }

                        entity.damage(player.getDamageSources().playerAttack(player), chargeDamage);

                        world.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                                SoundEvents.ITEM_TRIDENT_HIT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    }
                }
            }, 5);

            player.getItemCooldownManager().set(this, 20);
        }, 10);
    }

    private void performSweepAttack(World world, PlayerEntity player, float damage) {
        Box sweepBox = new Box(
                player.getX() - 2, player.getY() - 1, player.getZ() - 2,
                player.getX() + 2, player.getY() + 1, player.getZ() + 2
        );

        List<LivingEntity> nearbyEntities = world.getNonSpectatingEntities(
                LivingEntity.class, sweepBox);

        for (LivingEntity entity : nearbyEntities) {
            if (entity != player && entity.distanceTo(player) <= 3.0f) {
                entity.damage(player.getDamageSources().playerAttack(player), damage);
            }
        }

        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.0F, 1.0F);

        spawnSweepParticles(world, player);
    }

    private Entity getFirstEntityInLine(PlayerEntity player, double distance) {
        Vec3d eyePos = player.getEyePos();
        Vec3d lookVec = player.getRotationVec(1.0F);
        Vec3d endPos = eyePos.add(lookVec.multiply(distance));

        Box box = new Box(eyePos, endPos).expand(0.5);

        Entity closest = null;
        double closestDistance = Double.MAX_VALUE;

        for (Entity entity : player.getWorld().getOtherEntities(player, box)) {
            if (entity instanceof LivingEntity) {
                double entityDist = entity.squaredDistanceTo(eyePos);
                if (entityDist < closestDistance) {
                    closestDistance = entityDist;
                    closest = entity;
                }
            }
        }

        return closest;
    }

    private double getReachDistance(PlayerEntity player) {
        return player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
    }

    private void spawnUpwardParticles(World world, Vec3d position) {
        if (world instanceof ServerWorld serverWorld) {
            for (int i = 0; i < 10; i++) {
                double offsetX = world.random.nextDouble() * 0.5 - 0.25;
                double offsetZ = world.random.nextDouble() * 0.5 - 0.25;
                serverWorld.spawnParticles(
                        ParticleTypes.CRIT,
                        position.x + offsetX,
                        position.y + 0.5,
                        position.z + offsetZ,
                        1, 0, 0.2, 0, 0.1
                );
            }
        }
    }

    private void spawnSweepParticles(World world, PlayerEntity player) {
        if (world instanceof ServerWorld serverWorld) {
            Vec3d lookVec = player.getRotationVec(1.0F);
            Vec3d rightVec = lookVec.rotateY((float)Math.toRadians(-90));

            for (int i = 0; i < 8; i++) {
                double angle = Math.toRadians(i * 22.5);
                double radius = 2.0;

                double x = player.getX() + lookVec.x * radius * Math.cos(angle) + rightVec.x * radius * Math.sin(angle);
                double z = player.getZ() + lookVec.z * radius * Math.cos(angle) + rightVec.z * radius * Math.sin(angle);

                serverWorld.spawnParticles(
                        ParticleTypes.SWEEP_ATTACK,
                        x, player.getY() + 0.5, z,
                        1, 0, 0, 0, 0
                );
            }
        }
    }

    private void spawnChargeParticles(World world, PlayerEntity player) {
        if (world instanceof ServerWorld serverWorld) {
            for (int i = 0; i < 20; i++) {
                double offsetX = world.random.nextDouble() * 0.8 - 0.4;
                double offsetY = world.random.nextDouble() * 0.8 - 0.4;
                double offsetZ = world.random.nextDouble() * 0.8 - 0.4;

                serverWorld.spawnParticles(
                        ParticleTypes.BUBBLE_COLUMN_UP,
                        player.getX() + offsetX,
                        player.getY() + 0.5 + offsetY,
                        player.getZ() + offsetZ,
                        1, 0, 0, 0, 0.05
                );
            }
        }
    }

    private void scheduleTask(Runnable task, int delayTicks) {
        new Thread(() -> {
            try {
                Thread.sleep(delayTicks * 50L);
                task.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}