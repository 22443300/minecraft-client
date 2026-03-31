package dev.phantom.client.core.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class DoubleSetting extends Setting<Double> {

    private final double min;
    private final double max;
    private final double step;

    public DoubleSetting(String name, String description, double defaultValue, double min, double max) {
        this(name, description, defaultValue, min, max, 0.1);
    }

    public DoubleSetting(String name, String description, double defaultValue, double min, double max, double step) {
        super(name, description, defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
        this.value = clamp(defaultValue);
    }

    private double clamp(double v) {
        return Math.max(min, Math.min(max, v));
    }

    @Override
    public void setValue(Double value) {
        this.value = clamp(value);
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getStep() {
        return step;
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(value);
    }

    @Override
    public void deserialize(JsonElement element) {
        this.value = clamp(element.getAsDouble());
    }
}
