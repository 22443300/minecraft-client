package dev.phantom.client.modules.world;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class AutoBuild extends Module {
    private final IntSetting delay = register(new IntSetting("Delay", "Delay between block placements in ticks", 2, 0, 20));
    private final BooleanSetting rotate = register(new BooleanSetting("Rotate", "Rotate towards placement position", true));
    private final BooleanSetting antiGravity = register(new BooleanSetting("AntiGravity", "Place blocks under gravity-affected blocks", false));
    private final BooleanSetting scaffold = register(new BooleanSetting("Scaffold", "Scaffold under the player while building", false));

    public AutoBuild() {
        super("AutoBuild", "Automatically builds structures", Category.WORLD);
    }
}
