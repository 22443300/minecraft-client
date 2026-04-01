package dev.phantom.client.modules.movement;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class Parkour extends Module {
    private final BooleanSetting jump = register(new BooleanSetting("Jump", "Automatically jump at edges", true));
    private final BooleanSetting onEdge = register(new BooleanSetting("OnEdge", "Only jump when on the edge of a block", true));
    private final BooleanSetting sprint = register(new BooleanSetting("Sprint", "Keep sprinting during parkour", true));

    public Parkour() {
        super("Parkour", "Automatically jumps at block edges", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
