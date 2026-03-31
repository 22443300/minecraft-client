package dev.phantom.client.core.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class IntSetting extends Setting<Integer> {

    private final int min;
    private final int max;

    public IntSetting(String name, String description, int defaultValue, int min, int max) {
        super(name, description, defaultValue);
        this.min = min;
        this.max = max;
        this.value = clamp(defaultValue);
    }

    private int clamp(int v) {
        return Math.max(min, Math.min(max, v));
    }

    @Override
    public void setValue(Integer value) {
        this.value = clamp(value);
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(value);
    }

    @Override
    public void deserialize(JsonElement element) {
        this.value = clamp(element.getAsInt());
    }
}
