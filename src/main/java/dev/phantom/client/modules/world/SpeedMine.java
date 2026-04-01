package dev.phantom.client.modules.world;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class SpeedMine extends Module {
    public static SpeedMine INSTANCE;

    private final DoubleSetting multiplier = register(new DoubleSetting("Multiplier", "Mining speed multiplier", 3.0, 1.1, 20.0, 0.1));
    private final BooleanSetting onlyPickaxe = register(new BooleanSetting("OnlyPickaxe", "Only apply speed boost when holding a pickaxe", false));

    public SpeedMine() {
        super("SpeedMine", "Increases mining speed", Category.WORLD);
        INSTANCE = this;
    }
}
