package dev.phantom.client.modules.visual;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class AntiBlind extends Module {
    private final BooleanSetting blindness = register(new BooleanSetting("Blindness", "Remove blindness effect", true));
    private final BooleanSetting darkness = register(new BooleanSetting("Darkness", "Remove darkness effect", true));
    private final BooleanSetting nausea = register(new BooleanSetting("Nausea", "Remove nausea effect", true));
    private final BooleanSetting slowness = register(new BooleanSetting("Slowness", "Remove slowness visual", false));
    private final BooleanSetting weakness = register(new BooleanSetting("Weakness", "Remove weakness visual", false));
    private final BooleanSetting mining_fatigue = register(new BooleanSetting("Mining Fatigue", "Remove mining fatigue visual", true));

    public AntiBlind() {
        super("AntiBlind", "Removes visual potion effects like blindness and darkness", Category.VISUAL);
    }
}
