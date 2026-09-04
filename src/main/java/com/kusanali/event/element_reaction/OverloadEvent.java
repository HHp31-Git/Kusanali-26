package com.kusanali.event.element_reaction;

import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

public class OverloadEvent {

    private static final int SWIRL_COOLDOWN = 20;

    /** 记录实体上次触发扩散的时间 */
    private static final Map<UUID, Long> LAST_SWIRL_TIME = new WeakHashMap<>();

    /** 实体 -> 效果首次获得时间 */
    private static final Map<UUID, Map<StatusEffect, Long>> APPLY_TIME =
            new WeakHashMap<>();

    /** 上一 tick 是否拥有某效果 */
    private static final Map<UUID, Map<StatusEffect, Boolean>> PREV =
            new WeakHashMap<>();

    /** 已触发反应的实体 */
    private static final Set<UUID> REACTED = Collections.newSetFromMap(new WeakHashMap<>());


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
                track(uuid, ModEffects.PYRO, entity, now);

                long lastSwirl = LAST_SWIRL_TIME.getOrDefault(uuid, 0L);
                if (now - lastSwirl >= SWIRL_COOLDOWN) {
                    StatusEffectInstance electro = entity.getStatusEffect(ModEffects.ELECTRO);
                    StatusEffectInstance pyro = entity.getStatusEffect(ModEffects.PYRO);

                    if (electro == null || pyro == null) {
                        REACTED.remove(uuid);
                    } else if (!REACTED.contains(uuid)) {
                        Map<StatusEffect, Long> times = APPLY_TIME.get(uuid);
                        if (times != null) {
                            boolean electroInfinite = electro.getDuration() == -1;
                            boolean pyroInfinite = pyro.getDuration() == -1;

                            if (electroInfinite && pyroInfinite) {
                                applyOverloadReaction(entity, world, world);
                                REACTED.add(uuid);
                            } else if (electroInfinite) {
                                entity.removeStatusEffect(ModEffects.PYRO);
                                times.remove(ModEffects.PYRO);
                                PREV.computeIfAbsent(uuid, k -> new HashMap<>()).put(ModEffects.PYRO, false);
                                applyOverloadReaction(entity, world, world);
                                REACTED.add(uuid);
                            } else if (pyroInfinite) {
                                entity.removeStatusEffect(ModEffects.ELECTRO);
                                times.remove(ModEffects.ELECTRO);
                                PREV.computeIfAbsent(uuid, k -> new HashMap<>()).put(ModEffects.ELECTRO, false);
                                applyOverloadReaction(entity, world, world);
                                REACTED.add(uuid);
                            } else {
                                long electroTime = times.getOrDefault(ModEffects.ELECTRO, 0L);
                                long pyroTime = times.getOrDefault(ModEffects.PYRO, 0L);
                                boolean pyroLater = (electroTime == pyroTime) || (electroTime < pyroTime);
                                StatusEffect earlierEffect = pyroLater ? ModEffects.PYRO : ModEffects.ELECTRO;
                                entity.removeStatusEffect(earlierEffect);
                                times.remove(earlierEffect);
                                PREV.computeIfAbsent(uuid, k -> new HashMap<>()).put(earlierEffect, false);
                                applyOverloadReaction(entity, world, world);
                                LAST_SWIRL_TIME.put(uuid, now);
                                REACTED.add(uuid);
                            }
                        }
                    }
                }

                PREV.computeIfAbsent(uuid, k -> new HashMap<>()).put(ModEffects.ELECTRO, entity.getStatusEffect(ModEffects.ELECTRO) != null);
                PREV.computeIfAbsent(uuid, k -> new HashMap<>()).put(ModEffects.PYRO, entity.getStatusEffect(ModEffects.PYRO) != null);
            }
        });
    }

    // 抽取反应应用逻辑为独立方法，避免代码冗余
    private static void applyOverloadReaction(LivingEntity entity, ServerWorld serverWorld, World world) {
        float damage = 2.5f;
        DamageSource src = ModDamageTypes.reaction_type_3(serverWorld);
        entity.damage(src, damage);
        Vec3d pos = entity.getPos();
        double x = pos.getX();
        double y = pos.getY() + 0.5;
        double z = pos.getZ();
        world.createExplosion(null, x, y, z, 0.5f, false, World.ExplosionSourceType.NONE);
        float yaw = entity.getYaw();
        double rad = Math.toRadians(yaw);
        double dx = -Math.sin(rad);
        double dz = Math.cos(rad);
        entity.takeKnockback(0.4, dx, dz);
    }

}
