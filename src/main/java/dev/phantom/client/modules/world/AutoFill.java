package dev.phantom.client.modules.world;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class AutoFill extends Module {
    private final StringSetting material = register(new StringSetting("Material", "Block to fill with", "minecraft:cobblestone"));
    private final IntSetting range = register(new IntSetting("Range", "Range to search for air blocks", 5, 1, 16));
    private final IntSetting delay = register(new IntSetting("Delay", "Delay between placements in ticks", 3, 0, 20));
    private final BooleanSetting confirmLarge = register(new BooleanSetting("ConfirmLarge", "Require confirmation before filling large areas", true));

    public AutoFill() {
        super("AutoFill", "Fills air blocks in a region with a chosen material", Category.WORLD);
    }
}
