package dev.phantom.client.modules.movement;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class SafeWalk extends Module {
    public static SafeWalk INSTANCE;

    private final BooleanSetting onlyOnEdges = register(new BooleanSetting("OnlyOnEdges", "Only prevent falling when on a block edge", true));
    private final BooleanSetting air = register(new BooleanSetting("Air", "Apply safe walk while in the air", false));

    public SafeWalk() {
        super("SafeWalk", "Prevents walking off edges", Category.MOVEMENT);
        INSTANCE = this;
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
