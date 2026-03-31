package dev.phantom.client.core.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class EnumSetting<E extends Enum<E>> extends Setting<E> {

    private final Class<E> enumClass;

    public EnumSetting(String name, String description, E defaultValue, Class<E> enumClass) {
        super(name, description, defaultValue);
        this.enumClass = enumClass;
    }

    @SuppressWarnings("unchecked")
    public E[] getValues() {
        return enumClass.getEnumConstants();
    }

    public void next() {
        E[] values = getValues();
        int index = value.ordinal();
        this.value = values[(index + 1) % values.length];
    }

    public void prev() {
        E[] values = getValues();
        int index = value.ordinal();
        this.value = values[(index - 1 + values.length) % values.length];
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(value.name());
    }

    @Override
    public void deserialize(JsonElement element) {
        try {
            this.value = Enum.valueOf(enumClass, element.getAsString());
        } catch (IllegalArgumentException e) {
            this.value = defaultValue;
        }
    }
}
