package dev.phantom.client.modules.visual;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class MobESP extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "ESP render mode", "Box", new String[]{"Box", "Outline", "Corner"}));
    private final BooleanSetting passive = register(new BooleanSetting("Passive", "Show ESP for passive mobs", true));
    private final BooleanSetting neutral = register(new BooleanSetting("Neutral", "Show ESP for neutral mobs", true));
    private final BooleanSetting hostile = register(new BooleanSetting("Hostile", "Show ESP for hostile mobs", true));
    private final DoubleSetting range = register(new DoubleSetting("Range", "Mob ESP range", 32.0, 8.0, 128.0, 8.0));
    private final ColorSetting passiveColor = register(new ColorSetting("Passive Color", "Color for passive mobs", 0.0f, 1.0f, 0.0f, 1.0f));
    private final ColorSetting hostileColor = register(new ColorSetting("Hostile Color", "Color for hostile mobs", 1.0f, 0.0f, 0.0f, 1.0f));
    private final ColorSetting neutralColor = register(new ColorSetting("Neutral Color", "Color for neutral mobs", 1.0f, 1.0f, 0.0f, 1.0f));

    public MobESP() {
        super("MobESP", "ESP for mob entities with passive/neutral/hostile coloring", Category.VISUAL);
    }
}
