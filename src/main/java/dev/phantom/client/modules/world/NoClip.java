package dev.phantom.client.modules.world;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class NoClip extends Module {
    public static NoClip INSTANCE;

    private final DoubleSetting speed = register(new DoubleSetting("Speed", "Movement speed while clipping", 0.3, 0.1, 2.0, 0.1));
    private final ModeSetting mode = register(new ModeSetting("Mode", "NoClip method", "Push", new String[]{"Push", "Phase", "Full"}));

    public NoClip() {
        super("NoClip", "Clip through blocks", Category.WORLD);
        INSTANCE = this;
    }
}
