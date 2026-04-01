package dev.phantom.client.modules.movement;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class Jesus extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "Water walking method", "Solid", new String[]{"Solid", "Bounce", "OnGround"}));
    private final BooleanSetting lavaWalk = register(new BooleanSetting("LavaWalk", "Also walk on lava", false));
    private final BooleanSetting powderSnow = register(new BooleanSetting("PowderSnow", "Also walk on powder snow", true));

    public Jesus() {
        super("Jesus", "Walk on water and lava surfaces", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
