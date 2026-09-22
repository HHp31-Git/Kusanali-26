package com.kusanali.entity;

import com.kusanali.entity.custom.DendroSeedEntity;
import com.kusanali.entity.custom.HypostasisDendroEntity;
import com.kusanali.entity.custom.SeedProjectileEntity;
import com.kusanali.entity.original.GiantZombieEntity;
import com.kusanali.entity.original.ShroomboarEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;

public class ModEntities {
    public static final EntityType<DendroSeedEntity> DENDRO_SEED =
            //草种子
            Registry.register(
                    Registries.ENTITY_TYPE,
                    new Identifier("kusanali", "dendro_seed"),
                    FabricEntityTypeBuilder.create(SpawnGroup.MISC, DendroSeedEntity::new)
                            .dimensions(EntityDimensions.fixed(1.1f, 1.15f))
                            .trackRangeBlocks(64)
                            .trackedUpdateRate(3)
                            .build()
            );
    public static final EntityType<SeedProjectileEntity> SEED_PROJECTILE =
            //蔓生弹
            Registry.register(
                    Registries.ENTITY_TYPE,
                    new Identifier("kusanali", "seed_projectile"),
                    FabricEntityTypeBuilder.create(
                                    SpawnGroup.MISC, SeedProjectileEntity::new)
                            .dimensions(EntityDimensions.fixed(1.1f, 1.15f))
                            .trackRangeBlocks(64)
                            .trackedUpdateRate(20)
                            .build()
            );
    public static final EntityType<ShroomboarEntity> SHROOMBOAR =
            //蕈猪
            Registry.register(
                    Registries.ENTITY_TYPE,
                    new Identifier("kusanali", "shroomboar"),
                    FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, ShroomboarEntity::new)
                            .dimensions(EntityDimensions.fixed(0.9f, 0.9f))
                            .build()
            );
    public static final EntityType<HypostasisDendroEntity> HYPOSTASIS_DENDRO =
            //无相之草
            Registry.register(
                    Registries.ENTITY_TYPE,
                    new Identifier("kusanali", "hypostasis"),
                    FabricEntityTypeBuilder.create(
                                    SpawnGroup.MISC,
                                    HypostasisDendroEntity::new
                            ).dimensions(EntityDimensions.fixed(2.2f, 1.8f))
                            .fireImmune()
                            .build()
            );
    static {
        FabricDefaultAttributeRegistry.register(
                HYPOSTASIS_DENDRO,
                HypostasisDendroEntity.createHypostasisAttributes()
        );
    }
    public static final EntityType<GiantZombieEntity> GIANT_ZOMBIE =
            //巨人僵尸-盾
            Registry.register(
                    Registries.ENTITY_TYPE,
                    Identifier.of("kusanali", "giant_zombie"),
                    FabricEntityTypeBuilder.createMob()
                            .spawnGroup(SpawnGroup.MONSTER)
                            .entityFactory(GiantZombieEntity::new)
                            .dimensions(EntityDimensions.fixed(1.4F, 3.7F))
                            .trackRangeBlocks(64)
                            .trackedUpdateRate(2)
                            .spawnRestriction(
                                    SpawnRestriction.Location.ON_GROUND,
                                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                                    (type, world,
                                     reason, pos, random) ->
                                            GiantZombieEntity.canNaturallySpawn(world, pos, random)
                            )
                            .build()
            );

    public static void register() {
    }
}
