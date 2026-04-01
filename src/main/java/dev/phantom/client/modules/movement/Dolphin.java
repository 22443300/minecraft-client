package dev.phantom.client.modules.movement;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class Dolphin extends Module {
    private final DoubleSetting speed = register(new DoubleSetting("Speed", "Swimming speed multiplier", 1.5, 0.5, 5.0, 0.1));
    private final BooleanSetting sprint = register(new BooleanSetting("Sprint", "Sprint while swimming", true));

    public Dolphin() {
        super("Dolphin", "Increases swimming speed", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
