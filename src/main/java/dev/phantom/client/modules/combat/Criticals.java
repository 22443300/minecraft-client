package dev.phantom.client.modules.combat;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;

public class Criticals extends Module {

    public Criticals() {
        super("Criticals", "Ensures attacks are always critical hits", Category.COMBAT);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
