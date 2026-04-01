package dev.phantom.client.modules.movement;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class Fly extends Module {
    public static Fly INSTANCE;

    private final ModeSetting mode = register(new ModeSetting("Mode", "Fly mode", "Vanilla", new String[]{"Vanilla", "Packet", "Speed", "Glide", "Creative"}));
    private final DoubleSetting speed = register(new DoubleSetting("Speed", "Horizontal fly speed", 1.0, 0.1, 20.0, 0.1));
    private final DoubleSetting upSpeed = register(new DoubleSetting("UpSpeed", "Vertical fly speed", 1.0, 0.1, 10.0, 0.1));
    private final BooleanSetting antiKick = register(new BooleanSetting("AntiKick", "Prevent being kicked for flying", false));
    private final BooleanSetting strictCheck = register(new BooleanSetting("StrictCheck", "Use stricter anti-cheat evasion", false));

    public Fly() {
        super("Fly", "Allows free flight", Category.MOVEMENT);
        INSTANCE = this;
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
