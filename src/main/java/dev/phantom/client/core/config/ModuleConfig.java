package dev.phantom.client.core.config;

import com.google.gson.JsonObject;
import dev.phantom.client.core.module.Module;

public class ModuleConfig {

    private ModuleConfig() {}

    public static JsonObject serialize(Module module) {
        return module.serializeSettings();
    }

    public static void deserialize(Module module, JsonObject obj) {
        module.deserializeSettings(obj);
    }
}
