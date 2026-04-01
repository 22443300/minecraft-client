package dev.phantom.client.modules.visual;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class NoFog extends Module {
    private final BooleanSetting water = register(new BooleanSetting("Water", "Remove water fog", true));
    private final BooleanSetting lava = register(new BooleanSetting("Lava", "Remove lava fog", true));
    private final BooleanSetting blind = register(new BooleanSetting("Blind", "Remove blindness fog", true));
    private final BooleanSetting terrain = register(new BooleanSetting("Terrain", "Remove terrain fog", false));
    private final ModeSetting mode = register(new ModeSetting("Mode", "Fog removal mode", "Full", new String[]{"Full", "Partial"}));

    public static NoFog INSTANCE;

    public NoFog() {
        super("NoFog", "Removes all fog effects", Category.VISUAL);
        INSTANCE = this;
    }
}
