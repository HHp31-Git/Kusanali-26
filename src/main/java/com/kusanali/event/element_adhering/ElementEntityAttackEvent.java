package com.kusanali.event.element_adhering;

import com.kusanali.datagenerator.EntityTypeTagsProvider;
import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.kusanali.register.ModEffects.BASE_ELEMENTS;

public class ElementEntityAttackEvent {
    // 防重入
    private static final Set<UUID> REENTERING = ConcurrentHashMap.newKeySet();

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((target, source, amount) -> {
            if (target.getWorld().isClient()) return true;
            if (!(source.getAttacker() instanceof LivingEntity attacker)) return true;

            // 仅处理带有元素生物标签的攻击者
            if (!attacker.getType().isIn(EntityTypeTagsProvider.ELEMENTS_ENTITY)) return true;

            // 寻找攻击者身上的基础元素附着
            StatusEffect elementEffect = null;
            for (StatusEffectInstance instance : attacker.getStatusEffects()) {
                if (BASE_ELEMENTS.contains(instance.getEffectType()) && instance.getDuration() == -1) {
                    elementEffect = instance.getEffectType();
                    break;
                }
            }

            // 如果攻击者没有元素附着，走原版伤害逻辑
            if (elementEffect == null) return true;

            if (!(target.getWorld() instanceof ServerWorld world)) return true;

            UUID uuid = target.getUuid();
            if (REENTERING.contains(uuid)) return true;

            REENTERING.add(uuid);
            try {
                // 转换为元素伤害
                DamageSource elementSource = getElementDamageSource(world, elementEffect);
                target.damage(elementSource, amount);

                // 附着元素效果
                StatusEffectInstance existing = target.getStatusEffect(elementEffect);
                if (existing == null || existing.getDuration() <= 80) {
                    target.addStatusEffect(new StatusEffectInstance(elementEffect, 200, 0, false, true, true));
                }
            } finally {
                world.getServer().execute(() -> REENTERING.remove(uuid));
            }

            return false;
        });
    }

    // 元素 Effect 到 元素 DamageSource 的映射
    private static DamageSource getElementDamageSource(ServerWorld world, StatusEffect effect) {
        if (effect == ModEffects.PYRO) return ModDamageTypes.pyro(world);
        if (effect == ModEffects.CYRO) return ModDamageTypes.cyro(world);
        if (effect == ModEffects.HYDRO) return ModDamageTypes.hydro(world);
        if (effect == ModEffects.ELECTRO) return ModDamageTypes.electro(world);
        if (effect == ModEffects.DENDRO) return ModDamageTypes.dendro(world);
        if (effect == ModEffects.ANEMO) return ModDamageTypes.anemo(world);
        if (effect == ModEffects.GEO) return ModDamageTypes.geo(world);
        return ModDamageTypes.element(world);
    }
}
