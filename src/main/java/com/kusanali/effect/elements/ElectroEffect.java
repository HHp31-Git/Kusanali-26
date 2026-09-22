package com.kusanali.effect.elements;

import com.kusanali.define.event.ElementReaction;
import com.kusanali.event.element_reaction.ReactionRegistry;
import com.kusanali.register.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;

public class ElectroEffect extends StatusEffect {
    public ElectroEffect() {
        super(StatusEffectCategory.NEUTRAL, 0x6957C2);
    }
    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onApplied(entity, attributes, amplifier);
        if (!entity.getWorld().isClient()) {
            ServerWorld world = (ServerWorld) entity.getWorld();
            for (ElementReaction reaction : ReactionRegistry.getReactions()) {
                reaction.checkAndReact(entity, world, ModEffects.ELECTRO);
            }
        }
    }
}
