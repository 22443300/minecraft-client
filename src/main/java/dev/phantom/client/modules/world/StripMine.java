package dev.phantom.client.modules.world;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class StripMine extends Module {
    private final IntSetting spacing = register(new IntSetting("Spacing", "Spacing between strip mine tunnels", 3, 1, 8));
    private final IntSetting depth = register(new IntSetting("Depth", "Length of each strip mine tunnel", 50, 10, 200));
    private final IntSetting height = register(new IntSetting("Height", "Height of the tunnel to mine", 2, 1, 4));
    private final BooleanSetting torches = register(new BooleanSetting("Torches", "Automatically place torches while mining", true));
    private final IntSetting delay = register(new IntSetting("Delay", "Delay between block breaks in ticks", 2, 0, 20));

    public StripMine() {
        super("StripMine", "Automatically strip mines in a straight line", Category.WORLD);
    }
}
