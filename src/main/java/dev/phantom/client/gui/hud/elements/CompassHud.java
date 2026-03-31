package dev.phantom.client.gui.hud.elements;

import dev.phantom.client.gui.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/**
 * CompassHud – draws a horizontal compass strip showing all eight cardinal and
 * intercardinal directions.  The segment matching the player's current facing
 * is highlighted in white with a blue underline; all others are drawn in grey.
 *
 * <p>Default position: top-centre of the screen.  The X coordinate is
 * recomputed dynamically each frame to keep the strip centred.
 */
public class CompassHud extends HudElement {

    private static final String[] DIRS = { "N", "NE", "E", "SE", "S", "SW", "W", "NW" };
    private static final int SEGMENT_W = 18;

    public CompassHud() {
        super("compass", "Compass", 0, 4);
    }

    @Override
    public void updateSize() {
        this.width  = DIRS.length * SEGMENT_W;
        this.height = 16;
    }

    @Override
    public void render(DrawContext ctx, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.player == null) return;

        // Centre horizontally on screen every frame
        if (mc.getWindow() != null) {
            x = (mc.getWindow().getScaledWidth() - width) / 2;
        }

        // Normalise player yaw to 0-360
        float yaw = ((mc.player.getYaw(tickDelta) % 360f) + 360f) % 360f;

        // Map yaw to one of 8 direction segments.
        // MC yaw: 0 = south, 90 = west, 180/-180 = north, 270/-90 = east.
        // Segment index within DIRS (N, NE, E, SE, S, SW, W, NW):
        //   yaw 0   → S  (index 4)
        //   yaw 45  → SW (index 5)
        //   yaw 90  → W  (index 6)
        //   yaw 135 → NW (index 7)
        //   yaw 180 → N  (index 0)
        //   yaw 225 → NE (index 1)
        //   yaw 270 → E  (index 2)
        //   yaw 315 → SE (index 3)
        int[] yawToDirIndex = { 4, 5, 6, 7, 0, 1, 2, 3 };
        int rawSegment = (int) Math.floor((yaw + 22.5f) / 45f) % 8;
        int current = yawToDirIndex[rawSegment];

        // Background
        ctx.fill(x, y, x + width, y + height, 0xBB0D0D0D);

        int trHeight = mc.textRenderer.fontHeight;
        for (int i = 0; i < DIRS.length; i++) {
            int sx = x + i * SEGMENT_W;
            boolean isCurrent = (i == current);
            int color = isCurrent ? 0xFFFFFFFF : 0xFF666666;

            if (isCurrent) {
                ctx.fill(sx, y, sx + SEGMENT_W, y + height, 0xFF1A1A1A);
            }

            int tw = mc.textRenderer.getWidth(DIRS[i]);
            ctx.drawText(mc.textRenderer, DIRS[i],
                    sx + (SEGMENT_W - tw) / 2,
                    y + (height - trHeight) / 2,
                    color, true);
        }

        // Blue underline on the current facing segment
        ctx.fill(
                x + current * SEGMENT_W,
                y + height - 2,
                x + current * SEGMENT_W + SEGMENT_W,
                y + height,
                0xFF4A9EFF);
    }
}
