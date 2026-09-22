package com.kusanali.register;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class ModGameRules {
    public static final GameRules.Key<GameRules.BooleanRule> REINFORCE =
            GameRuleRegistry.register(
                    "EntityReinforcement",
                    GameRules.Category.MOBS,
                    GameRuleFactory.createBooleanRule(true)
            );

    public static void initialize() {}
}
