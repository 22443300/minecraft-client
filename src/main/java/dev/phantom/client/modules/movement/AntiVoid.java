package dev.phantom.client.modules.movement;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class AntiVoid extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "Anti-void method", "Packet", new String[]{"Packet", "Teleport", "Phase"}));
    private final DoubleSetting threshold = register(new DoubleSetting("Threshold", "Y level at which to trigger anti-void", -60.0, -200.0, 0.0, 5.0));

    public AntiVoid() {
        super("AntiVoid", "Prevents falling into the void", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
