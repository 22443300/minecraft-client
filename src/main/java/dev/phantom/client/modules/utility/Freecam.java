package dev.phantom.client.modules.utility;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class Freecam extends Module {
    private final DoubleSetting speed = register(new DoubleSetting("Speed", "Camera movement speed", 1.0, 0.1, 10.0, 0.1));
    private final BooleanSetting interact = register(new BooleanSetting("Interact", "Allow interaction while in freecam", true));
    private final BooleanSetting renderPlayer = register(new BooleanSetting("Render Player", "Render the player body while in freecam", true));
    private final BooleanSetting noClipCamera = register(new BooleanSetting("No Clip Camera", "Allow camera to pass through blocks", true));

    public Freecam() {
        super("Freecam", "Detach camera from player", Category.UTILITY);
    }
}
