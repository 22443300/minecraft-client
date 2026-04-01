package dev.phantom.client.modules.utility;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class AutoMount extends Module {
    private final BooleanSetting horses = register(new BooleanSetting("Horses", "Auto-mount horses", true));
    private final BooleanSetting donkeys = register(new BooleanSetting("Donkeys", "Auto-mount donkeys and mules", true));
    private final BooleanSetting boats = register(new BooleanSetting("Boats", "Auto-mount boats", true));
    private final BooleanSetting minecarts = register(new BooleanSetting("Minecarts", "Auto-mount minecarts", false));
    private final DoubleSetting range = register(new DoubleSetting("Range", "Range to detect nearby rideable entities", 4.0, 1.0, 8.0, 0.5));

    public AutoMount() {
        super("AutoMount", "Automatically mounts nearby rideable entities", Category.UTILITY);
    }
}
