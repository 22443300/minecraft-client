package dev.phantom.client.core.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.phantom.client.core.event.EventBus;
import dev.phantom.client.core.module.setting.Setting;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Module {

    private final String name;
    private final String description;
    private final Category category;
    private boolean enabled = false;
    private int keybind = GLFW.GLFW_KEY_UNKNOWN;
    private final List<Setting<?>> settings = new ArrayList<>();

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    protected <T extends Setting<?>> T register(T setting) {
        settings.add(setting);
        return setting;
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;
        this.enabled = enabled;
        if (enabled) {
            EventBus.INSTANCE.subscribe(this);
            onEnable();
        } else {
            EventBus.INSTANCE.unsubscribe(this);
            onDisable();
        }
    }

    public void onEnable() {}

    public void onDisable() {}

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getKeybind() {
        return keybind;
    }

    public void setKeybind(int keybind) {
        this.keybind = keybind;
    }

    public List<Setting<?>> getSettings() {
        return Collections.unmodifiableList(settings);
    }

    public JsonObject serializeSettings() {
        JsonObject obj = new JsonObject();
        obj.addProperty("enabled", enabled);
        obj.addProperty("keybind", keybind);
        JsonObject settingsObj = new JsonObject();
        for (Setting<?> setting : settings) {
            settingsObj.add(setting.getName(), setting.serialize());
        }
        obj.add("settings", settingsObj);
        return obj;
    }

    public void deserializeSettings(JsonObject obj) {
        if (obj.has("enabled")) {
            boolean wasEnabled = obj.get("enabled").getAsBoolean();
            if (wasEnabled != this.enabled) {
                setEnabled(wasEnabled);
            }
        }
        if (obj.has("keybind")) {
            this.keybind = obj.get("keybind").getAsInt();
        }
        if (obj.has("settings")) {
            JsonObject settingsObj = obj.getAsJsonObject("settings");
            for (Setting<?> setting : settings) {
                if (settingsObj.has(setting.getName())) {
                    JsonElement element = settingsObj.get(setting.getName());
                    setting.deserialize(element);
                }
            }
        }
    }
}
