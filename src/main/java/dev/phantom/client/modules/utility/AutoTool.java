package dev.phantom.client.modules.utility;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class AutoTool extends Module {
    private final BooleanSetting preferSilkTouch = register(new BooleanSetting("Prefer Silk Touch", "Prefer silk touch enchanted tools", false));
    private final BooleanSetting preferFortune = register(new BooleanSetting("Prefer Fortune", "Prefer fortune enchanted tools", true));
    private final BooleanSetting switchBack = register(new BooleanSetting("Switch Back", "Switch back to previous slot after mining", true));
    private final IntSetting delay = register(new IntSetting("Delay", "Ticks before switching tool", 0, 0, 5));

    public AutoTool() {
        super("AutoTool", "Automatically switches to the best tool for the block being mined", Category.UTILITY);
    }
}
