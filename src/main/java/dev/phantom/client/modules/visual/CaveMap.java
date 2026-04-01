package dev.phantom.client.modules.visual;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class CaveMap extends Module {
    private final IntSetting height = register(new IntSetting("Height", "Maximum Y level to render", 60, 0, 320));
    private final BooleanSetting hideSkylight = register(new BooleanSetting("Hide Skylight", "Hide blocks with sky access", true));
    private final IntSetting renderDistance = register(new IntSetting("Render Distance", "Cave map render distance in chunks", 8, 2, 32));

    public CaveMap() {
        super("CaveMap", "Shows underground terrain as a map", Category.VISUAL);
    }
}
