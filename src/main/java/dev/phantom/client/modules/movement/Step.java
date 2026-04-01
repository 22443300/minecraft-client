package dev.phantom.client.modules.movement;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class Step extends Module {
    private final DoubleSetting height = register(new DoubleSetting("Height", "Maximum step height", 1.0, 0.5, 3.0, 0.5));
    private final DoubleSetting speed = register(new DoubleSetting("Speed", "Step speed multiplier", 1.0, 0.1, 3.0, 0.1));
    private final ModeSetting mode = register(new ModeSetting("Mode", "Step mode", "Vanilla", new String[]{"Vanilla", "Packet", "NCP"}));
    private final BooleanSetting downStep = register(new BooleanSetting("DownStep", "Also step down blocks", false));

    public Step() {
        super("Step", "Allows stepping up blocks without jumping", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
