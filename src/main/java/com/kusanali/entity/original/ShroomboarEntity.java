package com.kusanali.entity.original;

import com.kusanali.entity.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class ShroomboarEntity extends PigEntity {
    public ShroomboarEntity(EntityType<? extends PigEntity> entityType, World world) {
        super(entityType, world);
    }
    public static DefaultAttributeContainer.Builder createShroomboarAttributes() {
        return createAnimalBaseAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0D);
    }
    public static DefaultAttributeContainer.Builder createAnimalBaseAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0D);
    }
    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isOf(Items.RED_MUSHROOM)
                || stack.isOf(Items.BROWN_MUSHROOM);
    }
    @Override
    public PigEntity createChild(ServerWorld world, PassiveEntity mate) {
        ShroomboarEntity child = ModEntities.SHROOMBOAR.create(world);
        if (child != null) {
            child.setBaby(true);
        }
        return child;
    }
    @Override
    protected void dropLoot(DamageSource source, boolean causedByPlayer) {
        super.dropLoot(source, causedByPlayer);
        if (this.random.nextFloat() < 0.4f) {
            this.dropItem(this.random.nextBoolean()
                    ? Items.RED_MUSHROOM : Items.BROWN_MUSHROOM, 1);
        }
    }
}
