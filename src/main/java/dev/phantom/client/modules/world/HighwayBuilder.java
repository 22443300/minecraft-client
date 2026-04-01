package dev.phantom.client.modules.world;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class HighwayBuilder extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "Highway preset mode", "Nether", new String[]{"Nether", "Overworld", "Custom"}));
    private final BooleanSetting fillFloor = register(new BooleanSetting("FillFloor", "Fill the floor with material", true));
    private final IntSetting width = register(new IntSetting("Width", "Width of the highway in blocks", 4, 1, 16));
    private final BooleanSetting mining = register(new BooleanSetting("Mining", "Automatically mine blocks in the path", true));
    private final BooleanSetting placing = register(new BooleanSetting("Placing", "Automatically place floor and wall blocks", true));
    private final StringSetting material = register(new StringSetting("Material", "Block material to use", "minecraft:obsidian"));

    public HighwayBuilder() {
        super("HighwayBuilder", "Automatically mines and builds highways", Category.WORLD);
    }
}
