package com.kusanali.event.element_reaction;

import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class GrowingIntensifyEvent {
    /** 冷却（18 tick = 0.9 秒） */
    private static final int COOLDOWN = 18;

    /** 效果首次获得时间 */
    private static final Map<UUID, Map<StatusEffect, Long>> APPLY_TIME = new WeakHashMap<>();

    /** 上一 tick 是否拥有 */
    private static final Map<UUID, Map<StatusEffect, Boolean>> PREV = new WeakHashMap<>();

    /** 已触发实体 */
    private static final Set<UUID> REACTED = Collections.newSetFromMap(new WeakHashMap<>());

    /** 记录实体上次触发的时间 */
    private static final Map<UUID, Long> LAST_TIME = new WeakHashMap<>();

    private static void track(UUID uuid, StatusEffect effect,
                              LivingEntity entity, long now) {
        Map<StatusEffect, Boolean> prevMap = PREV.get(uuid);
        boolean had = prevMap != null && prevMap.getOrDefault(effect, false);
        boolean has = entity.getStatusEffect(effect) != null;

        if (has && !had) {
            APPLY_TIME
                    .computeIfAbsent(uuid, k -> new HashMap<>())
                    .put(effect, now);
        }
    }
    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            long now = world.getTime();

            for (net.minecraft.entity.Entity ent : world.iterateEntities()) {
                if (!(ent instanceof LivingEntity entity)) continue;
                UUID uuid = entity.getUuid();

                track(uuid, ModEffects.INTENSIFY, entity, now);
                track(uuid, ModEffects.DENDRO, entity, now);

                long lastTime = LAST_TIME.getOrDefault(uuid, 0L);
                if (now - lastTime >= COOLDOWN) {
                    StatusEffectInstance intensify = entity.getStatusEffect(ModEffects.INTENSIFY);
                    StatusEffectInstance dendro = entity.getStatusEffect(ModEffects.DENDRO);

                    if (dendro == null || intensify == null) {
                        REACTED.remove(uuid);
                    } else if (!REACTED.contains(uuid)) {
                        Map<StatusEffect, Long> times = APPLY_TIME.get(uuid);
                        if (times != null) {
                            boolean dendroInfinite = dendro.getDuration() == -1;
                            boolean intensifyInfinite = intensify.getDuration() == -1;

                            if (dendroInfinite && intensifyInfinite) {
                                applyReaction(entity, world);
                                REACTED.add(uuid);
                            } else if (dendroInfinite) {
                                entity.removeStatusEffect(ModEffects.INTENSIFY);
                                times.remove(ModEffects.INTENSIFY);
                                PREV.computeIfAbsent(uuid, k -> new HashMap<>()).put(ModEffects.INTENSIFY, false);
                                applyReaction(entity, world);
                                REACTED.add(uuid);
                            } else if (intensifyInfinite) {
                                entity.removeStatusEffect(ModEffects.DENDRO);
                                times.remove(ModEffects.DENDRO);
                                PREV.computeIfAbsent(uuid, k -> new HashMap<>()).put(ModEffects.DENDRO, false);
                                applyReaction(entity, world);
                                REACTED.add(uuid);
                            } else {
                                entity.removeStatusEffect(ModEffects.INTENSIFY);
                                times.remove(ModEffects.INTENSIFY);
                                PREV.computeIfAbsent(uuid, k -> new HashMap<>()).put(ModEffects.INTENSIFY, false);
                                entity.removeStatusEffect(ModEffects.DENDRO);
                                times.remove(ModEffects.DENDRO);
                                PREV.computeIfAbsent(uuid, k -> new HashMap<>()).put(ModEffects.DENDRO, false);
                                applyReaction(entity, world);
                                REACTED.add(uuid);
                            }
                        }
                    }
                }

                // 修复：原代码错误地将 DENDRO 的状态写入了 ELECTRO 的键
                PREV.computeIfAbsent(uuid, k -> new HashMap<>()).put(ModEffects.DENDRO, entity.getStatusEffect(ModEffects.DENDRO) != null);
                PREV.computeIfAbsent(uuid, k -> new HashMap<>()).put(ModEffects.INTENSIFY, entity.getStatusEffect(ModEffects.INTENSIFY) != null);
            }
        });
    }

    private static void applyReaction(LivingEntity entity, ServerWorld world) {
        entity.damage(ModDamageTypes.reaction_type_4(world), 2.0f);
        // 粒子特效
        Vec3d center = entity.getPos();
        double cx = center.x;
        double cy = center.y + entity.getHeight() * 0.5;
        double cz = center.z;

        // 电火花粒子
        world.spawnParticles(
                ParticleTypes.ELECTRIC_SPARK,
                cx, cy, cz,
                8,
                0.3, 0.3, 0.3,
                0.08
        );

        // 绿色草粒子
        world.spawnParticles(
                ParticleTypes.HAPPY_VILLAGER,
                cx, cy, cz,
                12,
                0.4, 0.4, 0.4,
                0.16
        );
        // 更新最后触发时间
        LAST_TIME.put(entity.getUuid(), world.getTime());
    }
}
