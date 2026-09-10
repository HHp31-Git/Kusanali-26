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
        // ===== 烈焰人 =====
        DamageImmunityRegistry.addDamageTypeImmunity(
                EntityType.BLAZE,
                ModDamageTypes.ELEMENT_PYRO,
                0.0f
        );
        // ===== 岩浆怪 =====
        DamageImmunityRegistry.addDamageTypeImmunity(
                EntityType.MAGMA_CUBE,
                ModDamageTypes.ELEMENT_PYRO,
                0.0f
        );
        // ===== 雪傀儡 =====
        DamageImmunityRegistry.addDamageTypeImmunity(
                EntityType.SNOW_GOLEM,
                ModDamageTypes.ELEMENT_CYRO,
                0.0f
        );
        // ===== 铁傀儡 =====
        DamageImmunityRegistry.addDamageTypeImmunity(
                EntityType.IRON_GOLEM,
                ModDamageTypes.ELEMENT_GEO,
                0.0f
        );
    }
}
