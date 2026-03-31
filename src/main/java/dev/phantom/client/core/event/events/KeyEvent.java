package dev.phantom.client.core.event.events;

import dev.phantom.client.core.event.Event;
import org.lwjgl.glfw.GLFW;

public class KeyEvent extends Event {

    private final int key;
    private final int action;
    private final int mods;

    public KeyEvent(int key, int action, int mods) {
        this.key = key;
        this.action = action;
        this.mods = mods;
    }

    public int getKey() {
        return key;
    }

    public int getAction() {
        return action;
    }

    public int getMods() {
        return mods;
    }

    public boolean isPress() {
        return action == GLFW.GLFW_PRESS;
    }

    public boolean isRelease() {
        return action == GLFW.GLFW_RELEASE;
    }

    public boolean isRepeat() {
        return action == GLFW.GLFW_REPEAT;
    }
}
