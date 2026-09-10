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
     * 为生物添加同时对 DamageType 和弹射物的免疫
     */
    public static void addCombinedImmunity(EntityType<?> entityType, RegistryKey<DamageType> damageType,
                                           EntityType<?> projectileType, float multiplier) {
        addRule(entityType, new ImmunityRule()
                .setDamageType(damageType)
                .setProjectile(projectileType)
                .setMultiplier(multiplier));
    }

    /**
     * 移除某生物的所有免疫规则
     */
    public static void removeAllRules(EntityType<?> entityType) {
        RULES.remove(entityType);
    }

    /**
     * 移除某生物的特定免疫规则（按索引）
     */
    public static void removeRule(EntityType<?> entityType, int index) {
        List<ImmunityRule> rules = RULES.get(entityType);
        if (rules != null && index >= 0 && index < rules.size()) {
            rules.remove(index);
            if (rules.isEmpty()) {
                RULES.remove(entityType);
            }
        }
    }

    /**
     * 清除所有免疫规则
     */
    public static void clearAll() {
        RULES.clear();
    }

    /**
     * 查询生物对某伤害的最终倍率
     *
     * @param entityType     目标生物类型
     * @param damageTypeKey  伤害类型的 RegistryKey（可为 null）
     * @param projectileType 弹射物类型（可为 null）
     * @return 0.0 = 完全免疫, 0.5 = 减半, 1.0 = 正常
     */
    public static float getDamageMultiplier(EntityType<?> entityType,
                                            RegistryKey<DamageType> damageTypeKey,
                                            EntityType<?> projectileType) {
        List<ImmunityRule> rules = RULES.get(entityType);
        if (rules == null || rules.isEmpty()) {
            return 1.0f;
        }

        float multiplier = 1.0f;
        for (ImmunityRule rule : rules) {
            if (rule.matches(damageTypeKey, projectileType)) {
                multiplier = Math.min(multiplier, rule.multiplier);
            }
        }
        return multiplier;
    }

    /**
     * 获取某生物的所有免疫规则（用于调试/展示）
     */
    public static List<ImmunityRule> getRules(EntityType<?> entityType) {
        return RULES.getOrDefault(entityType, Collections.emptyList());
    }

    /**
     * 获取所有注册的免疫规则
     */
    public static Map<EntityType<?>, List<ImmunityRule>> getAllRules() {
        return Collections.unmodifiableMap(RULES);
    }

    /**
     * 检查某生物是否有任何免疫规则
     */
    public static boolean hasRules(EntityType<?> entityType) {
        return RULES.containsKey(entityType) && !RULES.get(entityType).isEmpty();
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

        public RegistryKey<DamageType> getDamageType() {
            return damageType;
        }

        public EntityType<?> getProjectileType() {
            return projectileType;
        }

        public float getMultiplier() {
            return multiplier;
        }

        /**
         * 判断此规则是否匹配给定的伤害
         */
        public boolean matches(RegistryKey<DamageType> damageTypeKey, EntityType<?> projType) {
            boolean damageMatches = (damageType == null) || damageType.equals(damageTypeKey);
            boolean projectileMatches = (projectileType == null) || projectileType.equals(projType);

            // 如果两者都设置了，需要同时匹配

            // 只有一个设置，匹配那个即可
            return damageMatches && projectileMatches;
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
