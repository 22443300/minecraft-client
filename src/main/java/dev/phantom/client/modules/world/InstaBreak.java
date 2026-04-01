package dev.phantom.client.modules.world;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class InstaBreak extends Module {
    public static InstaBreak INSTANCE;

    private final ModeSetting mode = register(new ModeSetting("Mode", "Insta break method", "Packet", new String[]{"Packet", "Creative", "SpeedMine"}));
    private final BooleanSetting onlyWhenHeld = register(new BooleanSetting("OnlyWhenHeld", "Only activate when holding a tool", false));

    public InstaBreak() {
        super("InstaBreak", "Breaks blocks instantly", Category.WORLD);
        INSTANCE = this;
    }
}
