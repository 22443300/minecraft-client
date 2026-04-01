package dev.phantom.client.modules.movement;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class Speed extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "Speed mode", "Strafe", new String[]{"Strafe", "BHop", "Vanilla", "YPort", "NCP"}));
    private final DoubleSetting speed = register(new DoubleSetting("Speed", "Speed multiplier", 1.2, 0.1, 5.0, 0.1));
    private final BooleanSetting onlyOnGround = register(new BooleanSetting("OnlyOnGround", "Only apply speed when on the ground", false));
    private final BooleanSetting checkGround = register(new BooleanSetting("CheckGround", "Check if player is on the ground before applying speed", true));

    public Speed() {
        super("Speed", "Increases movement speed", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
