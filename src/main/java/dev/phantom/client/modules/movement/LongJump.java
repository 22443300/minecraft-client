package dev.phantom.client.modules.movement;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class LongJump extends Module {
    private final DoubleSetting power = register(new DoubleSetting("Power", "Long jump power multiplier", 1.5, 1.0, 5.0, 0.1));
    private final ModeSetting mode = register(new ModeSetting("Mode", "Long jump mode", "Motion", new String[]{"Motion", "Packet", "Hypixel"}));
    private final BooleanSetting onlyOnGround = register(new BooleanSetting("OnlyOnGround", "Only activate when on the ground", true));

    public LongJump() {
        super("LongJump", "Increases jump distance", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
