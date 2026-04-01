package dev.phantom.client.modules.world;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class Excavator extends Module {
    private final IntSetting width = register(new IntSetting("Width", "Width of the excavation area", 3, 1, 16));
    private final IntSetting height = register(new IntSetting("Height", "Height of the excavation area", 3, 1, 16));
    private final IntSetting depth = register(new IntSetting("Depth", "Depth of the excavation area", 10, 1, 64));
    private final ModeSetting direction = register(new ModeSetting("Direction", "Direction to excavate", "Forward", new String[]{"Forward", "Down", "Selected"}));
    private final IntSetting delay = register(new IntSetting("Delay", "Delay between block breaks in ticks", 2, 0, 20));

    public Excavator() {
        super("Excavator", "Mines out a rectangular area automatically", Category.WORLD);
    }
}
