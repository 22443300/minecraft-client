package dev.phantom.client.modules.movement;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class Glide extends Module {
    private final DoubleSetting fallSpeed = register(new DoubleSetting("FallSpeed", "How fast you fall while gliding", 0.02, 0.001, 0.1, 0.001));
    private final BooleanSetting onlySneaking = register(new BooleanSetting("OnlySneaking", "Only glide while sneaking", false));

    public Glide() {
        super("Glide", "Reduces fall speed for a gliding effect", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
