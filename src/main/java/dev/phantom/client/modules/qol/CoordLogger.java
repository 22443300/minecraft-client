package dev.phantom.client.modules.qol;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;

public class CoordLogger extends Module {

    public CoordLogger() {
        super("CoordLogger", "Logs coordinates to a file", Category.QOL);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
