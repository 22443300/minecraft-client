package dev.phantom.client.modules.world;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class LitematicaMod extends Module {
    private final BooleanSetting autoPlace = register(new BooleanSetting("AutoPlace", "Automatically place blocks to match schematic", false));
    private final BooleanSetting verifyBlocks = register(new BooleanSetting("VerifyBlocks", "Verify placed blocks match the schematic", true));
    private final ModeSetting mode = register(new ModeSetting("Mode", "Litematica placement mode", "Normal", new String[]{"Normal", "Easy", "Printer"}));

    public LitematicaMod() {
        super("LitematicaMod", "Integrates Litematica functionality", Category.WORLD);
    }
}
