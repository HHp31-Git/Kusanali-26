package com.kusanali.event.damage;

import com.kusanali.datagenerator.EntityTypeTagsProvider;
import com.kusanali.entity.ModEntities;
import com.kusanali.register.ModDamageTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;

public class ModDamageImmunity {
    public static void registerAll() {
        // ===== 无相之草 =====
        DamageImmunityRegistry.addDamageTypeImmunity(
                ModEntities.HYPOSTASIS_DENDRO,
                ModDamageTypes.ELEMENT_DENDRO,
                0.0f
        );
        DamageImmunityRegistry.addProjectileImmunity(
                ModEntities.HYPOSTASIS_DENDRO,
                ModEntities.SEED_PROJECTILE,
                0.0f
        );
        DamageImmunityRegistry.addProjectileImmunity(
                ModEntities.HYPOSTASIS_DENDRO,
                ModEntities.DENDRO_SEED,
                0.0f
        );

        // ===== 基于元素标签的批量免疫注册 =====
        registerTagImmunity(EntityTypeTagsProvider.PYRO_ENTITY, ModDamageTypes.ELEMENT_PYRO);
        registerTagImmunity(EntityTypeTagsProvider.CYRO_ENTITY, ModDamageTypes.ELEMENT_CYRO);
        registerTagImmunity(EntityTypeTagsProvider.GEO_ENTITY, ModDamageTypes.ELEMENT_GEO);
        registerTagImmunity(EntityTypeTagsProvider.HYDRO_ENTITY, ModDamageTypes.ELEMENT_HYDRO);
        registerTagImmunity(EntityTypeTagsProvider.ELECTRO_ENTITY, ModDamageTypes.ELEMENT_ELECTRO);
        registerTagImmunity(EntityTypeTagsProvider.ANEMO_ENTITY, ModDamageTypes.ELEMENT_ANEMO);
    }

    /**
     * 遍历标签内的所有实体，统一注入对应元素伤害的免疫规则
     */
    private static void registerTagImmunity(TagKey<EntityType<?>> entityTag,
                                            RegistryKey<DamageType> damageType) {
        Registries.ENTITY_TYPE.getEntryList(entityTag)
                .stream()
                .flatMap(RegistryEntryList::stream)
                .map(RegistryEntry::value)
                .forEach(entityType -> DamageImmunityRegistry.addDamageTypeImmunity(entityType, damageType, 0.0f));
    }
}
