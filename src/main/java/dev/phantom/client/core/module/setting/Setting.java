package dev.phantom.client.core.module.setting;

import com.google.gson.JsonElement;

import java.util.function.Supplier;

public abstract class Setting<T> {

    protected String name;
    protected String description;
    protected T value;
    protected T defaultValue;
    protected Supplier<Boolean> visible = () -> true;

    public Setting(String name, String description, T defaultValue) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public Supplier<Boolean> getVisible() {
        return visible;
    }

    public void setVisible(Supplier<Boolean> visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible.get();
    }

    public abstract JsonElement serialize();

    public abstract void deserialize(JsonElement element);
}
