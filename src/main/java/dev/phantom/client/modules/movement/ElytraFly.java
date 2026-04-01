package dev.phantom.client.modules.movement;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class ElytraFly extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "Flight mode", "Vanilla", new String[]{"Vanilla", "Boost", "Pitch", "Packet"}));
    private final DoubleSetting speed = register(new DoubleSetting("Speed", "Flight speed", 1.8, 0.1, 10.0, 0.1));
    private final BooleanSetting autoLiftOff = register(new BooleanSetting("AutoLiftOff", "Automatically lift off when gliding", true));
    private final BooleanSetting boostOnJump = register(new BooleanSetting("BoostOnJump", "Boost when jumping", true));
    private final BooleanSetting antiVoid = register(new BooleanSetting("AntiVoid", "Prevent falling into the void", true));
    private final DoubleSetting maxPitch = register(new DoubleSetting("MaxPitch", "Maximum pitch angle", 10.0, 0.0, 90.0, 1.0));

    public ElytraFly() {
        super("ElytraFly", "Enhances elytra flight", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
