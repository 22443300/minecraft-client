package dev.phantom.client.modules.utility;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class RotationLock extends Module {
    private final DoubleSetting pitch = register(new DoubleSetting("Pitch", "Pitch angle to lock to", 90.0, -90.0, 90.0, 1.0));
    private final DoubleSetting yaw = register(new DoubleSetting("Yaw", "Yaw angle to lock to", 0.0, -180.0, 180.0, 1.0));
    private final BooleanSetting lockPitch = register(new BooleanSetting("Lock Pitch", "Lock the pitch (vertical) rotation", true));
    private final BooleanSetting lockYaw = register(new BooleanSetting("Lock Yaw", "Lock the yaw (horizontal) rotation", false));
    private final ModeSetting mode = register(new ModeSetting("Mode", "Rotation lock mode", "Exact", new String[]{"Exact", "Smooth", "Look"}));

    public RotationLock() {
        super("RotationLock", "Locks your head rotation to specific angles", Category.UTILITY);
    }
}
