package dev.phantom.client.gui.hud.elements;

import dev.phantom.client.gui.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;

/**
 * HUD element that displays the current client-to-server network latency
 * (ping) in milliseconds.
 */
public class PingHud extends HudElement {

    private static final int PADDING   = 4;
    private static final int BG_COLOR  = 0xAA000000;

    public PingHud() {
        super("ping", "Ping", 4, 70);
    }

    @Override
    public void updateSize() {
        MinecraftClient mc = MinecraftClient.getInstance();
        String text = buildText(mc);
        int textW = mc.textRenderer != null ? mc.textRenderer.getWidth(text) : 60;
        width  = textW + PADDING * 2;
        height = mc.textRenderer != null ? mc.textRenderer.fontHeight + PADDING * 2 : 20;
    }

    @Override
    public void render(DrawContext ctx, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        String text = buildText(mc);

        updateSize();

        ctx.fill(x, y, x + width, y + height, BG_COLOR);
        ctx.drawText(mc.textRenderer, text, x + PADDING, y + PADDING, pingColor(getPing(mc)), true);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static int getPing(MinecraftClient mc) {
        if (mc.player == null) return -1;
        ClientPlayNetworkHandler handler = mc.getNetworkHandler();
        if (handler == null) return -1;
        PlayerListEntry entry = handler.getPlayerListEntry(
                mc.player.getGameProfile().getId());
        return entry != null ? entry.getLatency() : -1;
    }

    private static String buildText(MinecraftClient mc) {
        int ping = getPing(mc);
        return ping >= 0 ? "Ping: " + ping + "ms" : "Ping: N/A";
    }

    private static int pingColor(int ping) {
        if (ping < 0)    return 0xFF888888; // N/A – grey
        if (ping <= 80)  return 0xFF4CAF50; // good – green
        if (ping <= 150) return 0xFFFFEB3B; // okay – yellow
        if (ping <= 300) return 0xFFFF9800; // high – orange
        return 0xFFF44336;                  // very high – red
    }
}
