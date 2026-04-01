package dev.phantom.client.modules.utility;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class AutoFarm extends Module {
    private final IntSetting range = register(new IntSetting("Range", "Block range to farm crops", 5, 1, 16));
    private final BooleanSetting replant = register(new BooleanSetting("Replant", "Automatically replant harvested crops", true));
    private final ModeSetting crop = register(new ModeSetting("Crop", "Type of crop to target", "All", new String[]{"All", "Wheat", "Carrot", "Potato", "Beetroot", "Melon", "Pumpkin"}));
    private final IntSetting delay = register(new IntSetting("Delay", "Ticks between farm actions", 3, 0, 20));
    private final BooleanSetting autoWalk = register(new BooleanSetting("Auto Walk", "Automatically walk to crops in range", false));

    public AutoFarm() {
        super("AutoFarm", "Automatically farms crops", Category.UTILITY);
    }
}
