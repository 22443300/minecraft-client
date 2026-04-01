package dev.phantom.client.modules.visual;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class BlockHighlight extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "Block highlight render mode", "Outline", new String[]{"Outline", "Fill", "Both"}));
    private final ColorSetting color = register(new ColorSetting("Color", "Highlight color", 1.0f, 1.0f, 1.0f, 0.3f));
    private final DoubleSetting thickness = register(new DoubleSetting("Thickness", "Outline thickness", 2.0, 0.5, 5.0, 0.1));
    private final BooleanSetting throughWalls = register(new BooleanSetting("Through Walls", "Show highlight through walls", false));

    public BlockHighlight() {
        super("BlockHighlight", "Highlights the block you are looking at", Category.VISUAL);
    }
}
