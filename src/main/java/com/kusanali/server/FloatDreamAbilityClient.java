package com.kusanali.server;

import com.kusanali.register.ModItems;
import com.kusanali.register.ModKeySet;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class FloatDreamAbilityClient implements HudRenderCallback {
    private static long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL = 50;
    private static float cachedPercentG = 0;
    private static float cachedPercentR = 0;
    private static boolean isCompleteG = false;
    private static boolean isCompleteR = false;

    public static long rCooldownEnd = 0;
    public static long gCooldownEnd = 0;
    public static long rCooldownStart = 0;
    public static long gCooldownStart = 0;

    public static void register() {
        // 注册 HUD 渲染
        HudRenderCallback.EVENT.register(new FloatDreamAbilityClient());

        // 注册客户端 Tick 事件
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ModKeySet.ACTIVATE_FLOAT_DREAM.wasPressed()) {
                if (isFloatDreamHeld(client.player)) {
                    ClientPlayNetworking.send(
                            new Identifier("kusanali", "activate_r_ability"),
                            new PacketByteBuf(Unpooled.buffer())
                    );
                }
            }

            if (ModKeySet.E_FLOAT_DREAM.wasPressed()) {
                if (isFloatDreamHeld(client.player)) {
                    ClientPlayNetworking.send(
                            new Identifier("kusanali", "activate_g_ability"),
                            new PacketByteBuf(Unpooled.buffer())
                    );
                }
            }
        });

        // 接收冷却同步
        ClientPlayNetworking.registerGlobalReceiver(
                new Identifier("kusanali", "r_cooldown_update"),
                (client, handler, buf, responseSender) -> {
                    long serverCooldownEnd = buf.readLong();
                    client.execute(() -> {
                        rCooldownEnd = serverCooldownEnd;
                        rCooldownStart = System.currentTimeMillis();
                    });
                });

        ClientPlayNetworking.registerGlobalReceiver(
                new Identifier("kusanali", "g_cooldown_update"),
                (client, handler, buf, responseSender) -> {
                    long serverCooldownEnd = buf.readLong();
                    client.execute(() -> {
                        gCooldownEnd = serverCooldownEnd;
                        gCooldownStart = System.currentTimeMillis();
                    });
                });

        // 断开连接时重置
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            rCooldownEnd = 0;
            gCooldownEnd = 0;
            rCooldownStart = 0;
            gCooldownStart = 0;
        });
    }

    private static boolean isFloatDreamHeld(net.minecraft.entity.player.PlayerEntity player) {
        if (player == null) return false;
        return player.getMainHandStack().getItem() == ModItems.FLOAT_DREAM
                || player.getOffHandStack().getItem() == ModItems.FLOAT_DREAM;
    }

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!isFloatDreamHeld(client.player)) {
            return;
        }

        TextRenderer textRenderer = client.textRenderer;
        long currentTime = System.currentTimeMillis();
        int windowWidth = client.getWindow().getScaledWidth();

        int baseY = 10;
        int lineHeight = textRenderer.fontHeight + 4;

        // 检查是否需要更新缓存
        if (currentTime - lastUpdateTime >= UPDATE_INTERVAL) {
            updateCooldownCache(currentTime);
            lastUpdateTime = currentTime;
        }

        // 渲染 G 技能（上方）
        renderCooldownHud(drawContext, textRenderer, windowWidth, baseY,
                "所识遍记 ", gCooldownEnd, gCooldownStart, currentTime, cachedPercentG, isCompleteG);

        // 渲染 R 技能（下方）
        renderCooldownHud(drawContext, textRenderer, windowWidth, baseY + lineHeight,
                "心景幻成 ", rCooldownEnd, rCooldownStart, currentTime, cachedPercentR, isCompleteR);
    }

    private void updateCooldownCache(long currentTime) {
        // 更新 G 技能缓存
        long gTotal = Math.max(gCooldownEnd - gCooldownStart, 1);
        long gElapsed = Math.max(currentTime - gCooldownStart, 0);
        cachedPercentG = gCooldownEnd == 0 || currentTime >= gCooldownEnd ?
                100.0f : Math.min(99.9f, (float) gElapsed / gTotal * 100);
        isCompleteG = cachedPercentG >= 100;

        // 更新 R 技能缓存
        long rTotal = Math.max(rCooldownEnd - rCooldownStart, 1);
        long rElapsed = Math.max(currentTime - rCooldownStart, 0);
        cachedPercentR = rCooldownEnd == 0 || currentTime >= rCooldownEnd ?
                100.0f : Math.min(99.9f, (float) rElapsed / rTotal * 100);
        isCompleteR = cachedPercentR >= 100;
    }

    private void renderCooldownHud(DrawContext drawContext, TextRenderer textRenderer,
                                   int windowWidth, int y, String prefix,
                                   long ignoredCooldownEnd, long ignoredCooldownStart, long ignoredCurrentTime,
                                   float cachedPercent, boolean isComplete) {
        int barWidth = 100;
        int barHeight = 12;
        int x = windowWidth - barWidth - 10;

        // 绘制背景条
        drawContext.fill(x, y, x + barWidth, y + barHeight, 0x905050);

        // 绘制填充条
        if (!isComplete && cachedPercent > 0) {
            renderGradientBar(drawContext, x, y, barWidth, barHeight, cachedPercent);
        }

        // 绘制边框
        drawContext.drawBorder(x, y, barWidth, barHeight, 0xFF0000);

        // 绘制文本
        String text = String.format("%s%.1f%%", prefix, cachedPercent);
        int textX = x + (barWidth - textRenderer.getWidth(text)) / 2;
        int textY = y + (barHeight - textRenderer.fontHeight) / 2;
        drawContext.drawTextWithShadow(textRenderer, text, textX, textY, 0xFFFFFF);
    }

    private void renderGradientBar(DrawContext drawContext, int x, int y, int width, int height, double percent) {
        int gradientSteps = 10;
        for (int i = 0; i < gradientSteps; i++) {
            double stepPercent = (double) i / gradientSteps;
            int stepWidth = (int) (width * (1 - stepPercent));
            int r = 0xFF;
            int g = percent == 100 ? 0xFF : (int) (0xFF * (percent / 100.0) * (1 - stepPercent));
            int color = (r << 16) | (g << 8);
            drawContext.fill(x, y, x + stepWidth, y + height, color);
        }
    }
}
