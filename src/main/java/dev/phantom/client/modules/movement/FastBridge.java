package dev.phantom.client.modules.movement;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class FastBridge extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "Bridging method", "Legit", new String[]{"Legit", "Telly", "GodBridge", "Eagle"}));
    private final IntSetting delay = register(new IntSetting("Delay", "Delay between block placements in ticks", 0, 0, 5));
    private final BooleanSetting onlySneaking = register(new BooleanSetting("OnlySneaking", "Only bridge while sneaking", false));
    private final ModeSetting towerMode = register(new ModeSetting("TowerMode", "Method used for towering up", "Jump", new String[]{"Jump", "Scaffold"}));

    public FastBridge() {
        super("FastBridge", "Helps place blocks faster while bridging", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
