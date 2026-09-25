package com.kusanali.event.damage;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DamageImmunityRegistry {

    // entity_type -> [免疫规则列表]
    private static final Map<EntityType<?>, List<ImmunityRule>> RULES = new ConcurrentHashMap<>();

    /**
     * 为生物添加一条免疫规则
     */
    public static void addRule(EntityType<?> entityType, ImmunityRule rule) {
        RULES.computeIfAbsent(entityType, k -> Collections.synchronizedList(new ArrayList<>())).add(rule);
    }

    /**
     * 为生物添加对某 DamageType 的免疫（快捷方法）
     */
    public static void addDamageTypeImmunity(EntityType<?> entityType,
                                             RegistryKey<DamageType> damageType, float multiplier) {
        addRule(entityType, new ImmunityRule()
                .setDamageType(damageType)
                .setMultiplier(multiplier));
    }

    /**
     * 为生物添加对某弹射物的免疫（快捷方法）
     */
    public static void addProjectileImmunity(EntityType<?> entityType, EntityType<?> projectileType, float multiplier) {
        addRule(entityType, new ImmunityRule()
                .setProjectile(projectileType)
                .setMultiplier(multiplier));
    }

    /**
     * 免疫规则类（链式调用风格）
     */
    public static class ImmunityRule {
        private RegistryKey<DamageType> damageType;  // null = 不检查 DamageType
        private EntityType<?> projectileType;         // null = 不检查弹射物
        private float multiplier = 0.0f;              // 默认完全免疫

        public ImmunityRule setDamageType(RegistryKey<DamageType> damageType) {
            this.damageType = damageType;
            return this;
        }

        public ImmunityRule setProjectile(EntityType<?> projectileType) {
            this.projectileType = projectileType;
            return this;
        }

        public ImmunityRule setMultiplier(float multiplier) {
            this.multiplier = multiplier;
            return this;
        }

        @Override
        public String toString() {
            return "ImmunityRule{" +
                    "damageType=" + (damageType != null ? damageType.getValue() : "ANY") +
                    ", projectile=" + (projectileType != null ? EntityType.getId(projectileType) : "ANY") +
                    ", multiplier=" + multiplier +
                    '}';
        }
    }
}
