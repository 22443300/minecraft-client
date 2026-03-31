package dev.phantom.client.modules.movement;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;

public class NoSlow extends Module {

    public NoSlow() {
        super("NoSlow", "Prevents slowing effects while using items", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
