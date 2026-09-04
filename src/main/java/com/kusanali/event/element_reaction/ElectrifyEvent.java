package com.kusanali.event.element_reaction;

import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

public class ElectrifyEvent {

    /** 扩散冷却时间（14 tick = 0.7 秒） */
    private static final int SWIRL_COOLDOWN = 60;

    /** 记录实体上次触发扩散的时间 */
    private static final Map<UUID, Long> LAST_SWIRL_TIME = new WeakHashMap<>();

    /** 实体 -> 效果首次获得时间 */
    private static final Map<UUID, Map<StatusEffect, Long>> APPLY_TIME =
            new WeakHashMap<>();

    /** 上一 tick 是否拥有效果 */
    private static final Map<UUID, Map<StatusEffect, Boolean>> PREV =
            new WeakHashMap<>();

    /** 已触发反应的实体 */
    private static final Set<UUID> REACTED = Collections.newSetFromMap(new WeakHashMap<>());

    /** Electrified 持续时间 */
    private static final int ELECTRIFIED_DURATION = 200;

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

            for (var ent : world.iterateEntities()) {
                if (!(ent instanceof LivingEntity entity)) continue;
                UUID uuid = entity.getUuid();

                track(uuid, ModEffects.ELECTRO, entity, now);
                track(uuid, ModEffects.HYDRO, entity, now);

                long lastSwirl = LAST_SWIRL_TIME.getOrDefault(uuid, 0L);
                if (now - lastSwirl >= SWIRL_COOLDOWN) {
                    if (entity.hasStatusEffect(ModEffects.ELECTRIFY)) {
                        REACTED.remove(uuid);
                    } else {
                        StatusEffectInstance electro = entity.getStatusEffect(ModEffects.ELECTRO);
                        StatusEffectInstance hydro = entity.getStatusEffect(ModEffects.HYDRO);

                        if (electro == null || hydro == null) {
                            REACTED.remove(uuid);
                        } else if (!REACTED.contains(uuid)) {
                            Map<StatusEffect, Long> times = APPLY_TIME.get(uuid);
                            if (times != null) {
                                boolean electroInfinite = electro.getDuration() == -1;
                                boolean hydroInfinite = hydro.getDuration() == -1;

                                if (electroInfinite && hydroInfinite) {
                                    applyElectrifiedReaction(entity, world);
                                    LAST_SWIRL_TIME.put(uuid, now);
                                    REACTED.add(uuid);
                                } else if (electroInfinite) {
                                    entity.removeStatusEffect(ModEffects.HYDRO);
                                    times.remove(ModEffects.HYDRO);
                                    PREV.computeIfAbsent(uuid, k -> new HashMap<>()).put(ModEffects.HYDRO, false);
                                    applyElectrifiedReaction(entity, world);
                                    LAST_SWIRL_TIME.put(uuid, now);
                                    REACTED.add(uuid);
                                } else if (hydroInfinite) {
                                    entity.removeStatusEffect(ModEffects.ELECTRO);
                                    times.remove(ModEffects.ELECTRO);
                                    PREV.computeIfAbsent(uuid, k -> new HashMap<>()).put(ModEffects.ELECTRO, false);
                                    applyElectrifiedReaction(entity, world);
                                    LAST_SWIRL_TIME.put(uuid, now);
                                    REACTED.add(uuid);
                                } else {
                                    entity.removeStatusEffect(ModEffects.HYDRO);
                                    times.remove(ModEffects.HYDRO);
                                    PREV.computeIfAbsent(uuid, k -> new HashMap<>()).put(ModEffects.HYDRO, false);
                                    applyElectrifiedReaction(entity, world);
                                    LAST_SWIRL_TIME.put(uuid, now);
                                    REACTED.add(uuid);
                                }
                            }
                        }
                    }
                }

                PREV.computeIfAbsent(uuid, k -> new HashMap<>()).put(ModEffects.ELECTRO, entity.getStatusEffect(ModEffects.ELECTRO) != null);
                PREV.computeIfAbsent(uuid, k -> new HashMap<>()).put(ModEffects.HYDRO, entity.getStatusEffect(ModEffects.HYDRO) != null);
            }
        });
    }


    private static void applyElectrifiedReaction(LivingEntity entity, ServerWorld world) {
        entity.addStatusEffect(
                new StatusEffectInstance(
                        ModEffects.ELECTRIFY,
                        ELECTRIFIED_DURATION,
                        0,
                        false,
                        true,
                        true
                )
        );
        double tx = entity.getX();
        double ty = entity.getY() + entity.getHeight() * 0.5;
        double tz = entity.getZ();
        entity.damage(ModDamageTypes.reaction_type_2(world), 1.0f);
        world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, tx, ty, tz,
                3, 0.1, 0.1, 0.1, 0.05);
    }
}
