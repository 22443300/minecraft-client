package dev.phantom.client.modules.utility;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class AutoSprint extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "Sprint mode", "Omni", new String[]{"Omni", "Legit", "Combat", "Always"}));
    private final BooleanSetting stopOnHit = register(new BooleanSetting("Stop On Hit", "Stop sprinting when taking damage", false));
    private final BooleanSetting stopEating = register(new BooleanSetting("Stop Eating", "Stop sprinting while eating", true));

    public AutoSprint() {
        super("AutoSprint", "Automatically sprints", Category.UTILITY);
    }
}
