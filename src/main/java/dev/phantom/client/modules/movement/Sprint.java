package dev.phantom.client.modules.movement;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class Sprint extends Module {
    public static Sprint INSTANCE;

    private final ModeSetting mode = register(new ModeSetting("Mode", "Sprint mode", "Omni", new String[]{"Omni", "Legit", "Combat"}));
    private final BooleanSetting stopOnHit = register(new BooleanSetting("StopOnHit", "Stop sprinting when hit", false));
    private final BooleanSetting multiDir = register(new BooleanSetting("MultiDir", "Sprint in all movement directions", true));

    public Sprint() {
        super("Sprint", "Keeps player sprinting in all directions", Category.MOVEMENT);
        INSTANCE = this;
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
