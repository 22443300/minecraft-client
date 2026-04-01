package dev.phantom.client.modules.utility;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class AutoWalk extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "Direction to walk automatically", "Forward", new String[]{"Forward", "Spin", "Backwards", "Left", "Right"}));
    private final DoubleSetting speed = register(new DoubleSetting("Speed", "Walk speed multiplier", 1.0, 0.1, 5.0, 0.1));
    private final BooleanSetting jump = register(new BooleanSetting("Jump", "Automatically jump while walking", false));
    private final BooleanSetting stopOnEntity = register(new BooleanSetting("Stop On Entity", "Stop walking when an entity is in the way", false));
    private final BooleanSetting stopOnBlock = register(new BooleanSetting("Stop On Block", "Stop walking when a block is in the way", false));

    public AutoWalk() {
        super("AutoWalk", "Automatically walks in a direction", Category.UTILITY);
    }
}
