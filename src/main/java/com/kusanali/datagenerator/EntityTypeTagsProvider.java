package com.kusanali.datagenerator;

import com.kusanali.Kusanali;
import com.kusanali.entity.ModEntities;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class EntityTypeTagsProvider extends FabricTagProvider.EntityTypeTagProvider {


    public EntityTypeTagsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup>
            completableFuture) {
        super(output, completableFuture);
    }
    public static final TagKey<EntityType<?>> ELEMENTS_ENTITY =
            TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(Kusanali.MOD_ID, "elements_entity"));

    public static final TagKey<EntityType<?>> PYRO_ENTITY =
            TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(Kusanali.MOD_ID, "pyro_entity"));
    public static final TagKey<EntityType<?>> CYRO_ENTITY =
            TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(Kusanali.MOD_ID, "cyro_entity"));
    public static final TagKey<EntityType<?>> GEO_ENTITY =
            TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(Kusanali.MOD_ID, "geo_entity"));
    public static final TagKey<EntityType<?>> DENDRO_ENTITY =
            TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(Kusanali.MOD_ID, "dendro_entity"));
    public static final TagKey<EntityType<?>> ANEMO_ENTITY =
            TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(Kusanali.MOD_ID, "anemo_entity"));
    public static final TagKey<EntityType<?>> ELECTRO_ENTITY =
            TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(Kusanali.MOD_ID, "electro_entity"));
    public static final TagKey<EntityType<?>> HYDRO_ENTITY =
            TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(Kusanali.MOD_ID, "hydro_entity"));

    public static final TagKey<EntityType<?>> ELITE =
            TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(Kusanali.MOD_ID, "elite"));
    public static final TagKey<EntityType<?>> WORLD_BOSS =
            TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(Kusanali.MOD_ID, "world_boss"));

    public static final TagKey<EntityType<?>> ABYSS_ENTITY =
            TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(Kusanali.MOD_ID, "abyss"));

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ELEMENTS_ENTITY)
                .add(EntityType.SLIME)
                .add(EntityType.MAGMA_CUBE)
                .add(EntityType.BLAZE)
                .add(EntityType.STRIDER)
                .add(EntityType.SNOW_GOLEM)
                .add(EntityType.STRAY)
                .add(EntityType.SNIFFER)
                .add(EntityType.IRON_GOLEM)
                .add(ModEntities.HYPOSTASIS_DENDRO)
                .add(EntityType.GUARDIAN)
                .add(EntityType.ELDER_GUARDIAN)
                .add(EntityType.SQUID)
                .add(EntityType.GLOW_SQUID)
                .add(EntityType.AXOLOTL)
                .add(EntityType.TROPICAL_FISH)
                .add(EntityType.PUFFERFISH)
                .add(EntityType.SILVERFISH)
                .add(EntityType.COD)
                .add(EntityType.SALMON)
                .add(EntityType.TADPOLE)
                .add(EntityType.DROWNED)
                .add(EntityType.DOLPHIN)
                .add(EntityType.ENDER_DRAGON)
                .add(EntityType.ENDERMITE)
                .add(EntityType.SHULKER)
                .add(EntityType.ENDERMAN);

        getOrCreateTagBuilder(PYRO_ENTITY)
                .add(EntityType.MAGMA_CUBE)
                .add(EntityType.BLAZE)
                .add(EntityType.STRIDER);

        getOrCreateTagBuilder(CYRO_ENTITY)
                .add(EntityType.SNOW_GOLEM)
                .add(EntityType.STRAY);

        getOrCreateTagBuilder(GEO_ENTITY)
                .add(EntityType.SNIFFER)
                .add(EntityType.IRON_GOLEM);

        getOrCreateTagBuilder(DENDRO_ENTITY)
                .add(ModEntities.HYPOSTASIS_DENDRO);

        getOrCreateTagBuilder(HYDRO_ENTITY)
                .add(EntityType.GUARDIAN)
                .add(EntityType.ELDER_GUARDIAN)
                .add(EntityType.SQUID)
                .add(EntityType.GLOW_SQUID)
                .add(EntityType.AXOLOTL)
                .add(EntityType.TROPICAL_FISH)
                .add(EntityType.PUFFERFISH)
                .add(EntityType.SILVERFISH)
                .add(EntityType.COD)
                .add(EntityType.SALMON)
                .add(EntityType.TADPOLE)
                .add(EntityType.DROWNED)
                .add(EntityType.DOLPHIN);

        getOrCreateTagBuilder(ELECTRO_ENTITY)
                .add(EntityType.ENDER_DRAGON)
                .add(EntityType.ENDERMITE)
                .add(EntityType.SHULKER)
                .add(EntityType.ENDERMAN);

        getOrCreateTagBuilder(ANEMO_ENTITY);

        getOrCreateTagBuilder(ELITE)
                .add(ModEntities.GIANT_ZOMBIE);

        getOrCreateTagBuilder(WORLD_BOSS)
                .add(ModEntities.HYPOSTASIS_DENDRO);

        getOrCreateTagBuilder(ABYSS_ENTITY)
                .add(EntityType.WITHER_SKELETON)
                .add(EntityType.WITHER);

    }
}
