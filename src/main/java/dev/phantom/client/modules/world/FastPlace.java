package dev.phantom.client.modules.world;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class FastPlace extends Module {
    public static FastPlace INSTANCE;

    private final IntSetting delay = register(new IntSetting("Delay", "Delay between block placements in ticks", 0, 0, 20));
    private final BooleanSetting noRightClickDelay = register(new BooleanSetting("NoRightClickDelay", "Remove right-click cooldown", true));
    private final BooleanSetting simultaneous = register(new BooleanSetting("Simultaneous", "Place multiple blocks per tick", false));

    public FastPlace() {
        super("FastPlace", "Reduces block placement delay", Category.WORLD);
        INSTANCE = this;
    }
}
