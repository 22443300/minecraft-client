package dev.phantom.client.core.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class KeybindSetting extends Setting<Integer> {

    public KeybindSetting(String name, String description) {
        super(name, description, GLFW.GLFW_KEY_UNKNOWN);
    }

    public KeybindSetting(String name, String description, int defaultKey) {
        super(name, description, defaultKey);
    }

    public boolean isPressed() {
        if (value == GLFW.GLFW_KEY_UNKNOWN) return false;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getWindow() == null) return false;
        long handle = mc.getWindow().getHandle();
        return GLFW.glfwGetKey(handle, value) == GLFW.GLFW_PRESS;
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
