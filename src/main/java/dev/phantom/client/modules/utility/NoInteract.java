package dev.phantom.client.modules.utility;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;

public class NoInteract extends Module {

    public NoInteract() {
        super("NoInteract", "Prevents accidental interactions", Category.UTILITY);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
