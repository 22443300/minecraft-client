package dev.phantom.client.modules.movement;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;

public class NoFall extends Module {

    public NoFall() {
        super("NoFall", "Prevents fall damage", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
