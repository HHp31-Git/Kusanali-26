package com.kusanali.mixin.entity;

import com.kusanali.datagenerator.EntityTypeTagsProvider;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(LivingEntity.class)
public abstract class EliteDouble {
    @Inject(method = "dropLoot", at = @At("RETURN"))
    private void eliteDoubleDrop(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.getWorld().isClient) return;

        boolean elite = self.getCommandTags().contains("ELITE")
                || self.getType().isIn(EntityTypeTagsProvider.ELITE);
        if (!elite) return;

        ServerWorld world = (ServerWorld) self.getWorld();

        List<ItemEntity> mine = new ArrayList<>();
        mine.addAll(world.getEntitiesByClass(ItemEntity.class,
                self.getBoundingBox().expand(2.0),
                ie -> ie.isAlive() && sameDeath(ie, self)));

        for (ItemEntity original : mine) {
            ItemStack s = original.getStack();
            if (s.isEmpty()) continue;

            ItemStack dup = s.copy();
            ItemEntity extra = new ItemEntity(world, original.getX(), original.getY(), original.getZ(), dup);
            extra.setVelocity(
                    world.random.nextDouble() - 0.5,
                    0.15 + world.random.nextDouble() * 0.1,
                    world.random.nextDouble() - 0.5
            );
            world.spawnEntity(extra);
        }
    }

    @Unique
    private static boolean sameDeath(ItemEntity ie, LivingEntity dead) {
        return ie.getPos().distanceTo(dead.getPos()) < 2.0
                && ie.age <= 1;
    }
}
