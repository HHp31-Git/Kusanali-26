package com.kusanali.event.damage;

import com.kusanali.entity.ModEntities;
import com.kusanali.register.ModDamageTypes;
import net.minecraft.entity.EntityType;

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

        // ===== 火元素实体 =====
        DamageImmunityRegistry.addDamageTypeImmunity(
                EntityType.MAGMA_CUBE,
                ModDamageTypes.ELEMENT_PYRO,
                0.0f
        );
        DamageImmunityRegistry.addDamageTypeImmunity(
                EntityType.BLAZE,
                ModDamageTypes.ELEMENT_PYRO,
                0.0f
        );
        DamageImmunityRegistry.addDamageTypeImmunity(
                EntityType.STRIDER,
                ModDamageTypes.ELEMENT_PYRO,
                0.0f
        );

        // ===== 冰元素实体 =====
        DamageImmunityRegistry.addDamageTypeImmunity(
                EntityType.SNOW_GOLEM,
                ModDamageTypes.ELEMENT_CYRO,
                0.0f
        );
        DamageImmunityRegistry.addDamageTypeImmunity(
                EntityType.STRAY,
                ModDamageTypes.ELEMENT_CYRO,
                0.0f
        );

        // ===== 岩元素实体 =====
        DamageImmunityRegistry.addDamageTypeImmunity(
                EntityType.SNIFFER,
                ModDamageTypes.ELEMENT_GEO,
                0.0f
        );
        DamageImmunityRegistry.addDamageTypeImmunity(
                EntityType.IRON_GOLEM,
                ModDamageTypes.ELEMENT_GEO,
                0.0f
        );

        // ===== 水元素实体 =====
        DamageImmunityRegistry.addDamageTypeImmunity(
                EntityType.GUARDIAN,
                ModDamageTypes.ELEMENT_HYDRO,
                0.0f
        );
        DamageImmunityRegistry.addDamageTypeImmunity(
                EntityType.ELDER_GUARDIAN,
                ModDamageTypes.ELEMENT_HYDRO,
                0.0f
        );
        DamageImmunityRegistry.addDamageTypeImmunity(
                EntityType.SQUID,
                ModDamageTypes.ELEMENT_HYDRO,
                0.0f
        );
        DamageImmunityRegistry.addDamageTypeImmunity(
                EntityType.GLOW_SQUID,
                ModDamageTypes.ELEMENT_HYDRO,
                0.0f
        );
        DamageImmunityRegistry.addDamageTypeImmunity(
                EntityType.AXOLOTL,
                ModDamageTypes.ELEMENT_HYDRO,
                0.0f
        );
        DamageImmunityRegistry.addDamageTypeImmunity(
                EntityType.TROPICAL_FISH,
                ModDamageTypes.ELEMENT_HYDRO,
                0.0f
        );
        DamageImmunityRegistry.addDamageTypeImmunity(
                EntityType.PUFFERFISH,
                ModDamageTypes.ELEMENT_HYDRO,
                0.0f
        );
        DamageImmunityRegistry.addDamageTypeImmunity(
                EntityType.SILVERFISH,
                ModDamageTypes.ELEMENT_HYDRO,
                0.0f
        );
        DamageImmunityRegistry.addDamageTypeImmunity(
                EntityType.COD,
                ModDamageTypes.ELEMENT_HYDRO,
                0.0f
        );
        DamageImmunityRegistry.addDamageTypeImmunity(
                EntityType.SALMON,
                ModDamageTypes.ELEMENT_HYDRO,
                0.0f
        );
        DamageImmunityRegistry.addDamageTypeImmunity(
                EntityType.TADPOLE,
                ModDamageTypes.ELEMENT_HYDRO,
                0.0f
        );
        DamageImmunityRegistry.addDamageTypeImmunity(
                EntityType.DROWNED,
                ModDamageTypes.ELEMENT_HYDRO,
                0.0f
        );
        DamageImmunityRegistry.addDamageTypeImmunity(
                EntityType.DOLPHIN,
                ModDamageTypes.ELEMENT_HYDRO,
                0.0f
        );

        // ===== 雷元素实体 =====
        DamageImmunityRegistry.addDamageTypeImmunity(
                EntityType.ENDER_DRAGON,
                ModDamageTypes.ELEMENT_ELECTRO,
                0.0f
        );
        DamageImmunityRegistry.addDamageTypeImmunity(
                EntityType.ENDERMITE,
                ModDamageTypes.ELEMENT_ELECTRO,
                0.0f
        );
        DamageImmunityRegistry.addDamageTypeImmunity(
                EntityType.SHULKER,
                ModDamageTypes.ELEMENT_ELECTRO,
                0.0f
        );
        DamageImmunityRegistry.addDamageTypeImmunity(
                EntityType.ENDERMAN,
                ModDamageTypes.ELEMENT_ELECTRO,
                0.0f
        );
    }
}
