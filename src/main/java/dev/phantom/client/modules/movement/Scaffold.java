package dev.phantom.client.modules.movement;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class Scaffold extends Module {
    private final BooleanSetting tower = register(new BooleanSetting("Tower", "Enable tower mode", true));
    private final ModeSetting speedMode = register(new ModeSetting("SpeedMode", "Speed mode for scaffolding", "Normal", new String[]{"Normal", "Fast", "Legit"}));
    private final BooleanSetting safeWalk = register(new BooleanSetting("SafeWalk", "Prevent falling off edges while scaffolding", true));
    private final BooleanSetting autoJump = register(new BooleanSetting("AutoJump", "Automatically jump while scaffolding", false));
    private final BooleanSetting sprint = register(new BooleanSetting("Sprint", "Sprint while scaffolding", true));
    private final IntSetting delay = register(new IntSetting("Delay", "Delay between block placements in ticks", 0, 0, 5));
    private final BooleanSetting keepY = register(new BooleanSetting("KeepY", "Maintain current Y level while scaffolding", true));
    private final BooleanSetting rotation = register(new BooleanSetting("Rotation", "Auto-rotate to place blocks", true));

    public Scaffold() {
        super("Scaffold", "Automatically places blocks beneath you", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
