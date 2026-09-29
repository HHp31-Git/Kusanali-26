package com.kusanali.server;

import com.kusanali.register.ModItems;
import com.kusanali.register.ModKeySet;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;


public class FloatDreamAbilityClient implements HudRenderCallback {

    // ==================== 冷却状态变量 ====================
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

        // ==================== 接收冷却同步 ====================

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

        // ==================== 断开连接时重置 ====================
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

        // 防御性计算总冷却时长
        long gTotal = Math.max(gCooldownEnd - gCooldownStart, 1);
        long rTotal = Math.max(rCooldownEnd - rCooldownStart, 1);

        // 渲染 G 技能（上方）
        renderCooldownHud(drawContext, textRenderer, windowWidth, baseY,
                "所识遍记 ", gCooldownEnd, currentTime, gTotal);

        // 渲染 R 技能（下方）
        renderCooldownHud(drawContext, textRenderer, windowWidth, baseY + lineHeight,
                "心景幻成 ", rCooldownEnd, currentTime, rTotal);
    }

    /**
     * 绘制单个技能的冷却 HUD（百分比显示）
     */
    private void renderCooldownHud(DrawContext drawContext, TextRenderer textRenderer,
                                   int windowWidth, int y, String prefix,
                                   long cooldownEnd, long cooldownStart, long currentTime) {
        // 进度条尺寸配置
        int barWidth = 100;
        int barHeight = 12;
        int x = windowWidth - barWidth - 10;

        // 防御性计算：确保冷却时长至少为1毫秒
        long totalCooldown = Math.max(cooldownEnd - cooldownStart, 1);
        // 防御性计算：确保已过时间不小于0
        long elapsed = Math.max(currentTime - cooldownStart, 0);

        double percent;
        if (cooldownEnd == 0 || currentTime >= cooldownEnd) {
            percent = 100.0;
        } else {
            // 使用浮点数计算，避免整数溢出
            percent = Math.min(99.9, (double) elapsed / totalCooldown * 100);
        }

        // 1. 绘制背景条（深灰色半透明）
        drawContext.fill(x, y, x + barWidth, y + barHeight, 0x905050);

        // 2. 绘制填充条（渐变色）
        int fillWidth = (int) (barWidth * (percent / 100.0));
        if (fillWidth > 0) {
            // 根据百分比计算渐变色
            int r = 0xFF;
            int g = percent == 100 ? 0xFF : (int) (0xFF * (percent / 100.0));
            int b = 0;
            int fillColor = (r << 16) | (g << 8) | b;
            drawContext.fill(x, y, x + fillWidth, y + barHeight, fillColor);
        }

        // 3. 绘制边框（纯黑）
        drawContext.drawBorder(x, y, barWidth, barHeight, 0xFF0000);

        // 4. 绘制居中百分比文本（保留1位小数）
        String text = String.format("%s%.1f%%", prefix, percent);
        int textX = x + (barWidth - textRenderer.getWidth(text)) / 2;
        int textY = y + (barHeight - textRenderer.fontHeight) / 2;
        drawContext.drawTextWithShadow(textRenderer, text, textX, textY, 0xFFFFFF);
    }
}

