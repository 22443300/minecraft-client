package dev.phantom.client.modules.visual;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class FullBright extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "FullBright implementation mode", "Gamma", new String[]{"Gamma", "NightVision", "Both"}));
    private final DoubleSetting gammaValue = register(new DoubleSetting("Gamma Value", "Gamma override value", 15.0, 5.0, 100.0, 1.0));

    public static FullBright INSTANCE;

    public FullBright() {
        super("FullBright", "Makes the world fully lit", Category.VISUAL);
        INSTANCE = this;
    }
}
