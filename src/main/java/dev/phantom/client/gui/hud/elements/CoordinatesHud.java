package dev.phantom.client.gui.hud.elements;

import dev.phantom.client.gui.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;

/**
 * HUD element that displays the player's current world coordinates and
 * cardinal facing direction.
 */
public class CoordinatesHud extends HudElement {

    private static final int PADDING     = 4;
    private static final int LINE_HEIGHT = 10;
    private static final int BG_COLOR    = 0xAA000000;
    private static final int TEXT_COLOR  = 0xFFE8E8E8;

    public CoordinatesHud() {
        super("coordinates", "Coordinates", 4, 4);
    }

    @Override
    public void updateSize() {
        // Three lines of text + top/bottom padding
        width  = 140;
        height = LINE_HEIGHT * 3 + PADDING * 2 + 2;
    }

    @Override
    public void render(DrawContext ctx, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        PlayerEntity player = mc.player;
        double px = player.getX();
        double py = player.getY();
        double pz = player.getZ();

        String coordLine = "X: %.1f  Y: %.1f  Z: %.1f".formatted(px, py, pz);
        String facingLine = "Facing: " + getFacingDirection(player.getYaw());
        String biome = ""; // placeholder – biome lookup requires world/registry access

        updateSize();

        // Background
        ctx.fill(x, y, x + width, y + height, BG_COLOR);

        int textX = x + PADDING;
        int textY = y + PADDING;

        ctx.drawText(mc.textRenderer, coordLine,  textX, textY,                     TEXT_COLOR, true);
        ctx.drawText(mc.textRenderer, facingLine, textX, textY + LINE_HEIGHT + 1,   TEXT_COLOR, true);
    }

    /**
     * Converts a Minecraft yaw angle (degrees, -180..180) into a compass
     * direction string.
     */
    private static String getFacingDirection(float yaw) {
        // Normalise to 0..360
        float normalised = ((yaw % 360f) + 360f) % 360f;

        if (normalised < 22.5f  || normalised >= 337.5f) return "South (+Z)";
        if (normalised < 67.5f)                           return "South-West";
        if (normalised < 112.5f)                          return "West (-X)";
        if (normalised < 157.5f)                          return "North-West";
        if (normalised < 202.5f)                          return "North (-Z)";
        if (normalised < 247.5f)                          return "North-East";
        if (normalised < 292.5f)                          return "East (+X)";
        if (normalised < 337.5f)                          return "South-East";
        return "South (+Z)";
    }
}
