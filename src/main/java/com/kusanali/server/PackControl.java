package com.kusanali.server;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class PackControl {
    public static final Identifier PACK = new Identifier("kusanali", "kusanali_texture");

    public static boolean fancyTitleEnabled() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.getResourcePackManager() == null) return true;
        return !mc.getResourcePackManager().getEnabledNames().contains(PACK.toString());
    }
}
