package com.kusanali.entity.original;

import com.kusanali.entity.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
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
