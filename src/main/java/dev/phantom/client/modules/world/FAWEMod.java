package dev.phantom.client.modules.world;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class FAWEMod extends Module {
    private final BooleanSetting confirmLarge = register(new BooleanSetting("ConfirmLarge", "Require confirmation for large operations", true));
    private final IntSetting maxBlocks = register(new IntSetting("MaxBlocks", "Maximum blocks per FAWE operation", 1000000, 10000, 100000000));
    private final BooleanSetting async = register(new BooleanSetting("Async", "Process FAWE operations asynchronously", true));

    public FAWEMod() {
        super("FAWEMod", "Integrates FAWE functionality", Category.WORLD);
    }
}
