package dev.phantom.client.modules.movement;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class NoSlow extends Module {
    private final BooleanSetting blocking = register(new BooleanSetting("Blocking", "Remove slowdown while blocking with a shield", true));
    private final BooleanSetting sneaking = register(new BooleanSetting("Sneaking", "Remove slowdown while sneaking", true));
    private final BooleanSetting eating = register(new BooleanSetting("Eating", "Remove slowdown while eating", true));
    private final BooleanSetting webSlow = register(new BooleanSetting("WebSlow", "Remove slowdown from cobwebs", true));
    private final BooleanSetting soulsand = register(new BooleanSetting("Soulsand", "Remove slowdown from soul sand", true));
    private final ModeSetting mode = register(new ModeSetting("Mode", "NoSlow method", "Packet", new String[]{"Packet", "Strafe"}));

    public NoSlow() {
        super("NoSlow", "Removes movement speed reductions", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
