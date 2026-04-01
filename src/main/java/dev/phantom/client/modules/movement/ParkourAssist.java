package dev.phantom.client.modules.movement;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class ParkourAssist extends Module {
    private final BooleanSetting autoJump = register(new BooleanSetting("AutoJump", "Automatically jump at block edges", true));
    private final BooleanSetting autoSprint = register(new BooleanSetting("AutoSprint", "Automatically sprint during parkour", true));
    private final BooleanSetting strafeAssist = register(new BooleanSetting("StrafeAssist", "Assist with lateral movement correction", true));
    private final DoubleSetting jumpBoost = register(new DoubleSetting("JumpBoost", "Extra jump height boost", 0.0, 0.0, 1.0, 0.05));

    public ParkourAssist() {
        super("ParkourAssist", "Advanced parkour assistance with auto-jump and strafe correction", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
