package com.kusanali.event.element_reaction;

import com.kusanali.define.event.ElementReaction;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.ArrayList;
import java.util.List;

/**
 * 元素反应注册器，统一管理所有反应的 tick 调度
 */
public class ReactionRegistry {

    private static final List<ElementReaction> reactions = new ArrayList<>();

    public static void register(ElementReaction reaction) {
        reactions.add(reaction);
    }

    public static void init() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            long now = world.getTime();
            for (var ent : world.iterateEntities()) {
                if (!(ent instanceof net.minecraft.entity.LivingEntity entity)) continue;
                for (ElementReaction reaction : reactions) {
                    reaction.tickEntity(entity, world, now);
                }
            }
        });
    }
}