package dev.phantom.client.gui.hud.elements;

import dev.phantom.client.gui.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/**
 * HUD element that shows the current client-side frames-per-second counter.
 */
public class FpsHud extends HudElement {

    private static final int PADDING   = 4;
    private static final int BG_COLOR  = 0xAA000000;
    private static final int TEXT_COLOR = 0xFFE8E8E8;

    public FpsHud() {
        super("fps", "FPS", 4, 30);
    }

    @Override
    public void updateSize() {
        MinecraftClient mc = MinecraftClient.getInstance();
        String text = buildText(mc);
        int textWidth = mc.textRenderer != null ? mc.textRenderer.getWidth(text) : 60;
        width  = textWidth + PADDING * 2;
        height = mc.textRenderer != null ? mc.textRenderer.fontHeight + PADDING * 2 : 20;
    }

    @Override
    public void render(DrawContext ctx, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        String text = buildText(mc);

        updateSize();

        // Background
        ctx.fill(x, y, x + width, y + height, BG_COLOR);

        // FPS text
        ctx.drawText(mc.textRenderer, text, x + PADDING, y + PADDING, fpsColor(mc.getCurrentFps()), true);
    }

    private static String buildText(MinecraftClient mc) {
        return "FPS: " + mc.getCurrentFps();
    }

    /** Colours the FPS value green/yellow/red based on performance thresholds. */
    private static int fpsColor(int fps) {
        if (fps >= 60) return 0xFF4CAF50; // green
        if (fps >= 30) return 0xFFFFEB3B; // yellow
        return 0xFFF44336;                 // red
    }
}
