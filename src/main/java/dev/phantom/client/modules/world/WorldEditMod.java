package dev.phantom.client.modules.world;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class WorldEditMod extends Module {
    private final BooleanSetting clipboard = register(new BooleanSetting("Clipboard", "Enable clipboard operations", true));
    private final BooleanSetting confirmPaste = register(new BooleanSetting("ConfirmPaste", "Require confirmation before large paste operations", true));
    private final IntSetting maxBlocks = register(new IntSetting("MaxBlocks", "Maximum number of blocks per operation", 100000, 1000, 10000000));

    public WorldEditMod() {
        super("WorldEditMod", "Integrates WorldEdit functionality", Category.WORLD);
    }
}
