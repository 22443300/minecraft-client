package dev.phantom.client.modules.visual;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class FreeLook extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "How free look is activated", "Hold", new String[]{"Hold", "Toggle"}));
    private final DoubleSetting yawLimit = register(new DoubleSetting("Yaw Limit", "Maximum horizontal look angle", 360.0, 30.0, 360.0, 10.0));
    private final DoubleSetting pitchLimit = register(new DoubleSetting("Pitch Limit", "Maximum vertical look angle", 90.0, 30.0, 90.0, 5.0));
    private final BooleanSetting smooth = register(new BooleanSetting("Smooth", "Smooth camera movement", true));

    public FreeLook() {
        super("FreeLook", "Look around without moving your character's body", Category.VISUAL);
    }
}
