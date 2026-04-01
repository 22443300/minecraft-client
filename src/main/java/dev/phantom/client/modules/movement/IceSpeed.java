package dev.phantom.client.modules.movement;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class IceSpeed extends Module {
    private final DoubleSetting multiplier = register(new DoubleSetting("Multiplier", "Speed multiplier on ice", 2.0, 1.0, 10.0, 0.1));
    private final BooleanSetting packed = register(new BooleanSetting("Packed", "Apply to packed ice", true));
    private final BooleanSetting blue = register(new BooleanSetting("Blue", "Apply to blue ice", true));
    private final BooleanSetting frosted = register(new BooleanSetting("Frosted", "Apply to frosted ice", true));

    public IceSpeed() {
        super("IceSpeed", "Increases movement speed on ice blocks", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
