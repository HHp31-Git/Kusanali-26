package com.kusanali.event.entity;

import com.kusanali.entity.ModEntities;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.collection.Weight;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.SpawnSettings;

import java.util.Set;

public class ShroomboarSpawn {
    private static final Set<RegistryKey<Biome>> JUNGLE_BIOMES = Set.of(
            BiomeKeys.JUNGLE,
            BiomeKeys.SPARSE_JUNGLE,
            BiomeKeys.BAMBOO_JUNGLE
    );

    public static void register() {
        BiomeModifications.create(new net.minecraft.util.Identifier("kusanali", "replace_pig"))
                .add(ModificationPhase.REPLACEMENTS,
                        BiomeSelectors.includeByKey(JUNGLE_BIOMES),
                        ctx -> {
                            var spawns = ctx.getSpawnSettings();   // 1.20.1 正确

                            // 原版丛林猪权重 = 10
                            int pigW = 1;     // 10%
                            int jungleW = 9;  // 90%

                            spawns.removeSpawnsOfEntityType(EntityType.PIG);

                            spawns.addSpawn(SpawnGroup.CREATURE,
                                    new SpawnSettings.SpawnEntry(
                                            EntityType.PIG, Weight.of(pigW), 3, 4)
                            );

                            spawns.addSpawn(SpawnGroup.CREATURE,
                                    new SpawnSettings.SpawnEntry(
                                            ModEntities.SHROOMBOAR, Weight.of(jungleW), 3, 4)
                            );
                        });
    }
}
