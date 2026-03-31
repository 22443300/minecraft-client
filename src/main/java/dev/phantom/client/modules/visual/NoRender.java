package dev.phantom.client.modules.visual;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;

public class NoRender extends Module {

    public NoRender() {
        super("NoRender", "Disables various rendering elements", Category.VISUAL);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
