package com.kusanali.register;

import com.kusanali.define.event.ElementReaction;
import com.kusanali.event.element_reaction.*;

import java.util.ArrayList;
import java.util.List;

public class ModReactionRegistry {

    private static final List<ElementReaction> reactions = new ArrayList<>();

    public static void register(ElementReaction reaction) {
        reactions.add(reaction);
    }

    public static List<ElementReaction> getReactions() {
        return reactions;
    }

    public static void init() {
        register(new IceBreakEvent());
        register(new FreezingEvent());
        register(new ElectrifyEvent());
        register(new OverloadEvent());
        register(new BoomEvent());
        register(new CrystallizationEvent());
        register(new DendroBurnEvent());
        register(new DiffusionEvent());
        register(new EvaporationEvent());
        register(new IceSpawnEvent());
        register(new IntensifyEvent());
        register(new MeltEvent());
        register(new SuperconductivityEventMain());
        register(new GrowingIntensifyEvent());
        register(new SuperIntensifyEvent());
    }
}