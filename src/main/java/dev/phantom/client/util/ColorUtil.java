package dev.phantom.client.util;

import java.awt.Color;

public final class ColorUtil {

    private ColorUtil() {}

    // -------------------------------------------------------------------------

    public static int fromRGB(int r, int g, int b) {
        return 0xFF000000 | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    public static int fromRGBA(int r, int g, int b, int a) {
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    public static int withAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | ((alpha & 0xFF) << 24);
    }

    // -------------------------------------------------------------------------
    // Channel extraction (float 0-1)
    // -------------------------------------------------------------------------

    public static float red(int color) {
        return ((color >> 16) & 0xFF) / 255f;
    }

    public static float green(int color) {
        return ((color >> 8) & 0xFF) / 255f;
    }

    public static float blue(int color) {
        return (color & 0xFF) / 255f;
    }

    public static float alpha(int color) {
        return ((color >> 24) & 0xFF) / 255f;
    }

    // -------------------------------------------------------------------------

    /**
     * Packs float RGBA components (0-1 each) into an ARGB int.
     */
    public static int toARGB(float r, float g, float b, float a) {
        return fromRGBA(
                (int)(r * 255f + 0.5f),
                (int)(g * 255f + 0.5f),
                (int)(b * 255f + 0.5f),
                (int)(a * 255f + 0.5f)
        );
    }

    /**
     * Linearly interpolates each ARGB channel between color1 and color2 by factor t (0-1).
     */
    public static int lerp(int color1, int color2, float t) {
        float a1 = alpha(color1), r1 = red(color1), g1 = green(color1), b1 = blue(color1);
        float a2 = alpha(color2), r2 = red(color2), g2 = green(color2), b2 = blue(color2);

        return toARGB(
                r1 + (r2 - r1) * t,
                g1 + (g2 - g1) * t,
                b1 + (b2 - b1) * t,
                a1 + (a2 - a1) * t
        );
    }

    /**
     * Returns a rainbow color that cycles over time.
     *
     * @param time       epoch-like timestamp in milliseconds (e.g. System.currentTimeMillis())
     * @param saturation HSB saturation (0-1)
     * @param brightness HSB brightness (0-1)
     */
    public static int rainbow(long time, float saturation, float brightness) {
        float hue = (time % 2000L) / 2000f;   // full cycle every 2 seconds
        int rgb = Color.HSBtoRGB(hue, saturation, brightness);
        // HSBtoRGB returns 0xFFRRGGBB; just return it (full alpha already set)
        return rgb;
    }

    /**
     * Inverts the RGB channels of a color while preserving alpha.
     */
    public static int invert(int color) {
        int a = (color >> 24) & 0xFF;
        int r = 255 - ((color >> 16) & 0xFF);
        int g = 255 - ((color >> 8)  & 0xFF);
        int b = 255 - (color         & 0xFF);
        return fromRGBA(r, g, b, a);
    }
}
