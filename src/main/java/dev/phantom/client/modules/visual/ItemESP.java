package dev.phantom.client.modules.visual;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class ItemESP extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "ESP render mode", "Box", new String[]{"Box", "Outline", "Glow"}));
    private final DoubleSetting range = register(new DoubleSetting("Range", "Item ESP range", 32.0, 4.0, 128.0, 4.0));
    private final BooleanSetting showName = register(new BooleanSetting("Show Name", "Show item name above", true));
    private final BooleanSetting showCount = register(new BooleanSetting("Show Count", "Show item stack count", true));
    private final ColorSetting color = register(new ColorSetting("Color", "Item ESP color", 1.0f, 1.0f, 0.0f, 1.0f));

    public ItemESP() {
        super("ItemESP", "Highlights dropped items on the ground", Category.VISUAL);
    }
}
