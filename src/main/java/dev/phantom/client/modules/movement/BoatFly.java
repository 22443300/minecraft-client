package dev.phantom.client.modules.movement;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class BoatFly extends Module {
    private final DoubleSetting speed = register(new DoubleSetting("Speed", "Boat fly speed", 1.0, 0.1, 5.0, 0.1));
    private final BooleanSetting vertical = register(new BooleanSetting("Vertical", "Allow vertical boat movement", true));

    public BoatFly() {
        super("BoatFly", "Allows flying while in a boat", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
