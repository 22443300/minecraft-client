package dev.phantom.client.core.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class ModeSetting extends Setting<String> {

    private final String[] modes;

    public ModeSetting(String name, String description, String defaultMode, String... modes) {
        super(name, description, defaultMode);
        this.modes = modes;
        this.value = defaultMode;
    }

    public String[] getModes() {
        return modes;
    }

    public void next() {
        int index = getCurrentIndex();
        this.value = modes[(index + 1) % modes.length];
    }

    public void prev() {
        int index = getCurrentIndex();
        this.value = modes[(index - 1 + modes.length) % modes.length];
    }

    private int getCurrentIndex() {
        for (int i = 0; i < modes.length; i++) {
            if (modes[i].equalsIgnoreCase(value)) return i;
        }
        return 0;
    }

    public boolean is(String mode) {
        return value.equalsIgnoreCase(mode);
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(value);
    }

    @Override
    public void deserialize(JsonElement element) {
        String v = element.getAsString();
        for (String mode : modes) {
            if (mode.equalsIgnoreCase(v)) {
                this.value = mode;
                return;
            }
        }
        this.value = defaultValue;
    }
}
