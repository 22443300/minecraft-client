package dev.phantom.client.modules.movement;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class NoFall extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "NoFall method", "Packet", new String[]{"Packet", "Anchor", "Bucket", "MLG"}));
    private final BooleanSetting placeBucket = register(new BooleanSetting("PlaceBucket", "Place water bucket to break fall", true));
    private final BooleanSetting onlyWhenFalling = register(new BooleanSetting("OnlyWhenFalling", "Only activate when actually falling", false));

    public NoFall() {
        super("NoFall", "Prevents fall damage", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
