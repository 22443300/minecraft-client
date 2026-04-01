package dev.phantom.client.modules.visual;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class SearchESP extends Module {
    private final StringSetting blockName = register(new StringSetting("Block ID", "Block ID to search for", "minecraft:diamond_ore"));
    private final IntSetting range = register(new IntSetting("Range", "Search range in blocks", 16, 4, 64));
    private final ColorSetting color = register(new ColorSetting("Color", "Highlight color", 0.2f, 0.8f, 1.0f, 1.0f));
    private final BooleanSetting showCount = register(new BooleanSetting("Show Count", "Show number of found blocks", true));

    public static SearchESP INSTANCE;

    public SearchESP() {
        super("SearchESP", "Highlights specific blocks by ID", Category.VISUAL);
        INSTANCE = this;
    }
}
