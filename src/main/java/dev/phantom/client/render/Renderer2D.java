package dev.phantom.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public final class Renderer2D {

    private Renderer2D() {}

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private static TextRenderer tr() {
        return MinecraftClient.getInstance().textRenderer;
    }

    // -------------------------------------------------------------------------
    // Rectangles
    // -------------------------------------------------------------------------

    public static void drawRect(DrawContext ctx, int x, int y, int w, int h, int color) {
        ctx.fill(x, y, x + w, y + h, color);
    }

    public static void drawOutlinedRect(DrawContext ctx, int x, int y, int w, int h, int borderColor, int fillColor) {
        // Fill interior
        ctx.fill(x + 1, y + 1, x + w - 1, y + h - 1, fillColor);
        // Top
        ctx.fill(x, y, x + w, y + 1, borderColor);
        // Bottom
        ctx.fill(x, y + h - 1, x + w, y + h, borderColor);
        // Left
        ctx.fill(x, y + 1, x + 1, y + h - 1, borderColor);
        // Right
        ctx.fill(x + w - 1, y + 1, x + w, y + h - 1, borderColor);
    }

    public static void drawHorizontalGradient(DrawContext ctx, int x, int y, int w, int h, int colorLeft, int colorRight) {
        ctx.fillGradient(x, y, x + w, y + h, colorLeft, colorRight);
    }

    public static void drawVerticalGradient(DrawContext ctx, int x, int y, int w, int h, int colorTop, int colorBottom) {
        // fillGradient(x1, y1, x2, y2, colorStart, colorEnd) - start is top-left, end is bottom-right
        // For vertical gradient we use the overload with z parameter and swap via axis
        ctx.fillGradient(x, y, x + w, y + h, colorTop, colorBottom);
    }

    /**
     * Approximate rounded rect by drawing a rect and cutting corners.
     */
    public static void drawRoundedRect(DrawContext ctx, int x, int y, int w, int h, int radius, int color) {
        int r = Math.min(radius, Math.min(w / 2, h / 2));

        // Center body
        ctx.fill(x + r, y, x + w - r, y + h, color);
        // Left body
        ctx.fill(x, y + r, x + r, y + h - r, color);
        // Right body
        ctx.fill(x + w - r, y + r, x + w, y + h - r, color);

        // Fill corner arcs by iterating
        for (int i = 0; i < r; i++) {
            // Use Bresenham-ish horizontal span per row
            double dx = Math.sqrt((double) r * r - (double)(r - i) * (r - i));
            int span = (int) Math.ceil(dx);

            // Top-left corner
            ctx.fill(x + r - span, y + i, x + r, y + i + 1, color);
            // Top-right corner
            ctx.fill(x + w - r, y + i, x + w - r + span, y + i + 1, color);
            // Bottom-left corner
            ctx.fill(x + r - span, y + h - i - 1, x + r, y + h - i, color);
            // Bottom-right corner
            ctx.fill(x + w - r, y + h - i - 1, x + w - r + span, y + h - i, color);
        }
    }

    // -------------------------------------------------------------------------
    // Text
    // -------------------------------------------------------------------------

    public static void drawText(DrawContext ctx, String text, int x, int y, int color, boolean shadow) {
        ctx.drawText(tr(), text, x, y, color, shadow);
    }

    public static void drawCenteredText(DrawContext ctx, String text, int cx, int y, int color, boolean shadow) {
        int textWidth = tr().getWidth(text);
        ctx.drawText(tr(), text, cx - textWidth / 2, y, color, shadow);
    }

    public static int getTextWidth(String text) {
        return tr().getWidth(text);
    }

    public static int getTextHeight() {
        return tr().fontHeight;
    }

    // -------------------------------------------------------------------------
    // Circle (pixel-perfect via Bresenham)
    // -------------------------------------------------------------------------

    public static void drawCircle(DrawContext ctx, int cx, int cy, int radius, int color) {
        int x = 0;
        int y = radius;
        int d = 3 - 2 * radius;

        while (y >= x) {
            // 8 symmetric points
            plotPixel(ctx, cx + x, cy + y, color);
            plotPixel(ctx, cx - x, cy + y, color);
            plotPixel(ctx, cx + x, cy - y, color);
            plotPixel(ctx, cx - x, cy - y, color);
            plotPixel(ctx, cx + y, cy + x, color);
            plotPixel(ctx, cx - y, cy + x, color);
            plotPixel(ctx, cx + y, cy - x, color);
            plotPixel(ctx, cx - y, cy - x, color);

            if (d < 0) {
                d += 4 * x + 6;
            } else {
                d += 4 * (x - y) + 10;
                y--;
            }
            x++;
        }
    }

    private static void plotPixel(DrawContext ctx, int x, int y, int color) {
        ctx.fill(x, y, x + 1, y + 1, color);
    }

    // -------------------------------------------------------------------------
    // Line (Bresenham)
    // -------------------------------------------------------------------------

    public static void drawLine(DrawContext ctx, int x1, int y1, int x2, int y2, int color, int width) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        int cx = x1, cy = y1;

        while (true) {
            int half = width / 2;
            ctx.fill(cx - half, cy - half, cx + half + 1, cy + half + 1, color);

            if (cx == x2 && cy == y2) break;
            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; cx += sx; }
            if (e2 <  dx) { err += dx; cy += sy; }
        }
    }

    // -------------------------------------------------------------------------
    // Texture
    // -------------------------------------------------------------------------

    public static void drawTexture(DrawContext ctx, Identifier tex, int x, int y, int w, int h) {
        ctx.drawTexture(tex, x, y, 0, 0, w, h, w, h);
    }
}
