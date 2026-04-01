package dev.phantom.client.modules.movement;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class Phase extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "Phase mode", "Vanilla", new String[]{"Vanilla", "Clip", "Phase"}));
    private final DoubleSetting speed = register(new DoubleSetting("Speed", "Phase movement speed", 0.5, 0.1, 5.0, 0.1));

    public Phase() {
        super("Phase", "Clips through blocks", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
