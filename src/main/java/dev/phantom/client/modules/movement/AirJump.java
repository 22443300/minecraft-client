package dev.phantom.client.modules.movement;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class AirJump extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "Air jump mode", "Full", new String[]{"Full", "Double"}));
    private final IntSetting delay = register(new IntSetting("Delay", "Delay between air jumps in ticks", 5, 1, 40));
    private final BooleanSetting stopOnGround = register(new BooleanSetting("StopOnGround", "Stop air jumping when landing on the ground", true));

    public AirJump() {
        super("AirJump", "Allows jumping in mid-air", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
