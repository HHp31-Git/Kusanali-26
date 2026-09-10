package com.kusanali.event.element_reaction;

import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

public class SuperconductivityEventMain {

    /** 扩散冷却时间（14 tick = 0.7 秒） */
    private static final int SWIRL_COOLDOWN = 20;

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

    /** Superconductivity 持续时间 */
    private static final int SUPERCONDUCTIVITY_DURATION = 220;

    /** 反应伤害 */
    private static final float REACTION_DAMAGE = 2.0f;

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            long now = world.getTime();

            for (var ent : world.iterateEntities()) {
                if (!(ent instanceof LivingEntity entity)) continue;
                UUID uuid = entity.getUuid();

                track(uuid, ModEffects.ELECTRO, entity, now);
                track(uuid, ModEffects.CYRO, entity, now);

                long lastSwirl = LAST_SWIRL_TIME.getOrDefault(uuid, 0L);
                if (now - lastSwirl >= SWIRL_COOLDOWN) {
                    if (entity.hasStatusEffect(ModEffects.SUPERCONDUCTIVITY)) {
                        REACTED.remove(uuid);
                    } else {
                        StatusEffectInstance electro = entity.getStatusEffect(ModEffects.ELECTRO);
                        StatusEffectInstance cyro = entity.getStatusEffect(ModEffects.CYRO);

                        if (electro == null || cyro == null) {
                            REACTED.remove(uuid);
                        } else if (!REACTED.contains(uuid)) {
                            Map<StatusEffect, Long> times = APPLY_TIME.get(uuid);
                            if (times != null) {
                                boolean electroInfinite = electro.getDuration() == -1;
                                boolean cyroInfinite = cyro.getDuration() == -1;

                                if (electroInfinite && cyroInfinite) {
                                    applySuperconductivityReaction(entity, world);
                                    REACTED.add(uuid);
                                } else if (electroInfinite) {
                                    entity.removeStatusEffect(ModEffects.CYRO);
                                    times.remove(ModEffects.CYRO);
                                    PREV.computeIfAbsent(uuid, k -> new HashMap<>()).put(ModEffects.CYRO, false);
                                    applySuperconductivityReaction(entity, world);
                                    REACTED.add(uuid);
                                } else if (cyroInfinite) {
                                    entity.removeStatusEffect(ModEffects.ELECTRO);
                                    times.remove(ModEffects.ELECTRO);
                                    PREV.computeIfAbsent(uuid, k -> new HashMap<>()).put(ModEffects.ELECTRO, false);
                                    applySuperconductivityReaction(entity, world);
                                    REACTED.add(uuid);
                                } else {
                                    long electroTime = times.getOrDefault(ModEffects.ELECTRO, 0L);
                                    long cyroTime = times.getOrDefault(ModEffects.CYRO, 0L);
                                    boolean electroLater = (electroTime == cyroTime) || (electroTime > cyroTime);
                                    StatusEffect earlierEffect = electroLater ? ModEffects.CYRO : ModEffects.ELECTRO;
                                    entity.removeStatusEffect(earlierEffect);
                                    times.remove(earlierEffect);
                                    PREV.computeIfAbsent(uuid, k -> new HashMap<>()).put(earlierEffect, false);
                                    applySuperconductivityReaction(entity, world);
                                    LAST_SWIRL_TIME.put(uuid, now);
                                    REACTED.add(uuid);
                                }
                            }
                        }
                    }
                }

                PREV.computeIfAbsent(uuid, k -> new HashMap<>()).put(ModEffects.ELECTRO, entity.getStatusEffect(ModEffects.ELECTRO) != null);
                PREV.computeIfAbsent(uuid, k -> new HashMap<>()).put(ModEffects.CYRO, entity.getStatusEffect(ModEffects.CYRO) != null);
            }
        });
    }


    /** 应用超导反应 */
    private static void applySuperconductivityReaction(LivingEntity entity, ServerWorld world) {
        // 造成伤害
        DamageSource src = ModDamageTypes.reaction_type_3(world);
        entity.damage(src, REACTION_DAMAGE);

        // 给予效果
        entity.addStatusEffect(new StatusEffectInstance(
                ModEffects.SUPERCONDUCTIVITY,
                SUPERCONDUCTIVITY_DURATION,
                0,
                false,
                false,
                true
        ));

        // 生成雪花粒子
        spawnSnowflakeParticles(entity, world);
    }

    /** 记录首次获得时间 */
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
    private static void spawnSnowflakeParticles(LivingEntity entity, ServerWorld world) {
        double x = entity.getX();
        double y = entity.getY() + entity.getHeight() * 0.5;
        double z = entity.getZ();

        world.spawnParticles(
                ParticleTypes.SNOWFLAKE,
                x, y, z,
                10,
                1.0,
                0.5,
                1.0,
                0.1
        );

        world.spawnParticles(
                ParticleTypes.SNOWFLAKE,
                x, y + 0.5, z,
                5,
                1.5,
                0.3,
                1.5,
                0.02
        );
    }
}
