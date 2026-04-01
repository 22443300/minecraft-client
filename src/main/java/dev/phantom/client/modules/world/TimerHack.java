package dev.phantom.client.modules.world;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class TimerHack extends Module {
    public static TimerHack INSTANCE;

    private final DoubleSetting speed = register(new DoubleSetting("Speed", "Timer speed multiplier", 2.0, 0.1, 10.0, 0.1));
    private final ModeSetting mode = register(new ModeSetting("Mode", "Which packet direction to affect", "Both", new String[]{"Both", "Send", "Recv"}));

    public TimerHack() {
        super("TimerHack", "Speeds up the game timer", Category.WORLD);
        INSTANCE = this;
    }
}
