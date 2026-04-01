package dev.phantom.client.modules.qol;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class TimerModule extends Module {
    private final DoubleSetting  speed         = register(new DoubleSetting ("Speed",        "Game tick speed multiplier",         1.0, 0.1, 10.0, 0.1));
    private final BooleanSetting onlyMovement  = register(new BooleanSetting("OnlyMovement", "Apply timer only to movement",       false));
    private final BooleanSetting onlyPackets   = register(new BooleanSetting("OnlyPackets",  "Apply timer only to packet sending", false));

    public static TimerModule INSTANCE;

    public TimerModule() {
        super("TimerModule", "Modifies game tick speed", Category.QOL);
        INSTANCE = this;
    }
}
