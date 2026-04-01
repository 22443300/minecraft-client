package dev.phantom.client.modules.world;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class AutoMine extends Module {
    private final IntSetting breakDelay = register(new IntSetting("BreakDelay", "Delay between block breaks in ticks", 3, 0, 20));
    private final BooleanSetting swing = register(new BooleanSetting("Swing", "Swing arm animation when mining", true));
    private final BooleanSetting rotate = register(new BooleanSetting("Rotate", "Rotate towards target block", true));
    private final ModeSetting mode = register(new ModeSetting("Mode", "Mining mode", "Packet", new String[]{"Packet", "Normal"}));
    private final BooleanSetting mineAir = register(new BooleanSetting("MineAir", "Send mine packets on air blocks", false));

    public AutoMine() {
        super("AutoMine", "Automatically mines blocks", Category.WORLD);
    }
}
