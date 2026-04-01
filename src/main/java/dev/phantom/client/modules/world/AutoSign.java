package dev.phantom.client.modules.world;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class AutoSign extends Module {
    private final StringSetting line1 = register(new StringSetting("Line1", "Sign line 1", "Phantom Client"));
    private final StringSetting line2 = register(new StringSetting("Line2", "Sign line 2", ""));
    private final StringSetting line3 = register(new StringSetting("Line3", "Sign line 3", ""));
    private final StringSetting line4 = register(new StringSetting("Line4", "Sign line 4", ""));

    public AutoSign() {
        super("AutoSign", "Automatically writes text on signs when placed", Category.WORLD);
    }
}
