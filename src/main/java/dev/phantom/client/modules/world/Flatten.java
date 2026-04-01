package dev.phantom.client.modules.world;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class Flatten extends Module {
    private final IntSetting range = register(new IntSetting("Range", "Horizontal range to flatten", 5, 1, 16));
    private final IntSetting depth = register(new IntSetting("Depth", "Depth of blocks to remove per layer", 1, 1, 5));
    private final IntSetting targetY = register(new IntSetting("TargetY", "Target Y level for flattening", 64, -64, 320));
    private final ModeSetting mode = register(new ModeSetting("Mode", "Flatten mode", "Down", new String[]{"Down", "Target Y", "Surface"}));

    public Flatten() {
        super("Flatten", "Automatically flattens terrain", Category.WORLD);
    }
}
