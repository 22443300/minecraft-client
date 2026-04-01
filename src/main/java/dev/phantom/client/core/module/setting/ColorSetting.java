package dev.phantom.client.core.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class ColorSetting extends Setting<Integer> {

    private final boolean hasAlpha;

    public ColorSetting(String name, String description, int defaultValue, boolean hasAlpha) {
        super(name, description, defaultValue);
        this.hasAlpha = hasAlpha;
    }

    public ColorSetting(String name, String description, int defaultValue) {
        this(name, description, defaultValue, true);
    }

    /** Convenience constructor using float RGBA components (each 0.0-1.0). */
    public ColorSetting(String name, String description, float r, float g, float b, float a) {
        this(name, description,
             ((int)(a * 255) << 24) | ((int)(r * 255) << 16) | ((int)(g * 255) << 8) | (int)(b * 255),
             true);
    }

    /** Convenience constructor using float RGB components with full opacity. */
    public ColorSetting(String name, String description, float r, float g, float b) {
        this(name, description, r, g, b, 1.0f);
    }

    public boolean isHasAlpha() {
        return hasAlpha;
    }

    public float r() {
        return ((value >> 16) & 0xFF) / 255.0f;
    }

    public float g() {
        return ((value >> 8) & 0xFF) / 255.0f;
    }

    public float b() {
        return (value & 0xFF) / 255.0f;
    }

    public float a() {
        if (!hasAlpha) return 1.0f;
        return ((value >> 24) & 0xFF) / 255.0f;
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(value);
    }

    @Override
    public void deserialize(JsonElement element) {
        this.value = element.getAsInt();
    }
}
