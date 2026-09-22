package com.kusanali.mixin.rule;

import com.kusanali.entity.ModEntities;
import com.kusanali.entity.original.GiantZombieEntity;
import com.kusanali.register.ModGameRules;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieEntity.class)
public abstract class GiantZombieSpawn {
    @Inject(method = "initialize", at = @At("HEAD"), cancellable = true)
    private void kusanali_tryReplace(
            ServerWorldAccess world,
            LocalDifficulty difficulty,
            SpawnReason spawnReason,
            @Nullable EntityData entityData,
            @Nullable NbtCompound entityNbt,
            CallbackInfoReturnable<EntityData> cir) {

        if (spawnReason != SpawnReason.NATURAL
                && spawnReason != SpawnReason.CHUNK_GENERATION
                && spawnReason != SpawnReason.SPAWNER) {
            return;
        }

        if (!(world instanceof ServerWorld sw)) return;

        if (!sw.getGameRules().getBoolean(ModGameRules.REINFORCE)) {
            return;
        }

        ZombieEntity self = (ZombieEntity)(Object)this;
        BlockPos pos = self.getBlockPos();

        if (pos.getY() < 30 || pos.getY() > 150) {
            return;
        }

        Random random = world.getRandom();
        if (random.nextFloat() >= 0.05F) {
            return;
        }

        GiantZombieEntity giant = new GiantZombieEntity(
                ModEntities.GIANT_ZOMBIE, world.toServerWorld());

        giant.refreshPositionAndAngles(
                self.getX(), self.getY(), self.getZ(),
                self.getYaw(), self.getPitch()
        );
        giant.initialize(world, difficulty, spawnReason, entityData, entityNbt);

        world.spawnEntity(giant);
        self.discard();
        cir.setReturnValue(entityData);
    }
}
