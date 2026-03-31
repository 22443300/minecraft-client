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
