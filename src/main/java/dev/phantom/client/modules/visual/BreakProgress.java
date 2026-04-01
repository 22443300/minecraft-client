package dev.phantom.client.modules.visual;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class BreakProgress extends Module {
    private final ColorSetting color = register(new ColorSetting("Color", "Break progress display color", 1.0f, 0.5f, 0.0f, 1.0f));
    private final BooleanSetting showPercentage = register(new BooleanSetting("Show Percentage", "Show percentage above block", true));
    private final BooleanSetting showOnAll = register(new BooleanSetting("Show On All", "Show progress on all breaking blocks", false));

    public BreakProgress() {
        super("BreakProgress", "Shows block break progress as a percentage above blocks", Category.VISUAL);
    }
}
