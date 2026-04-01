package dev.phantom.client.modules.visual;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class Ambience extends Module {
    private final BooleanSetting sky = register(new BooleanSetting("Sky", "Override sky color", false));
    private final ColorSetting skyColor = register(new ColorSetting("Sky Color", "Custom sky color", 0.1f, 0.1f, 0.2f, 1.0f));
    private final BooleanSetting fog = register(new BooleanSetting("Fog", "Override fog color", false));
    private final ColorSetting fogColor = register(new ColorSetting("Fog Color", "Custom fog color", 0.05f, 0.05f, 0.1f, 1.0f));
    private final BooleanSetting stars = register(new BooleanSetting("Stars", "Override star rendering", false));
    private final BooleanSetting rainbowSky = register(new BooleanSetting("Rainbow Sky", "Cycle sky through rainbow colors", false));

    public Ambience() {
        super("Ambience", "Customizes the world atmosphere and sky colors", Category.VISUAL);
    }
}
