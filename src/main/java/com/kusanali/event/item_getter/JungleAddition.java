package com.kusanali.event.item_getter;

import com.kusanali.register.ModItems;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import java.util.Set;

public class JungleAddition {
    private static final Set<RegistryKey<Biome>> JUNGLE_BIOMES = Set.of(
            BiomeKeys.JUNGLE,
            BiomeKeys.SPARSE_JUNGLE,
            BiomeKeys.BAMBOO_JUNGLE
    );
    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (!(damageSource.getAttacker() instanceof PlayerEntity)) return;
            if (!(entity instanceof Monster)) return;
            World world = entity.getWorld();
            if (world.isClient()) return;
            BlockPos pos = entity.getBlockPos();
            RegistryKey<Biome> biomeKey = world.getBiome(pos).getKey().orElse(null);
            if (biomeKey == null) return;
            if (!JUNGLE_BIOMES.contains(biomeKey)) return;
            int range = (int)(Math.random() * 5) + 1;
            if (range == 1) {
                world.spawnEntity(new ItemEntity(world,
                        pos.getX(), pos.getY(), pos.getZ(),
                        ModItems.EMERALD_SLIVER.getDefaultStack()));
            }
        });
    }
}
