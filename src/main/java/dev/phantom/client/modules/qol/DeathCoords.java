package dev.phantom.client.modules.qol;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class DeathCoords extends Module {
    private final BooleanSetting announce      = register(new BooleanSetting("Announce",      "Broadcast death coords in chat",    false));
    private final BooleanSetting logToFile     = register(new BooleanSetting("LogToFile",     "Save death coords to a log file",   true));
    private final BooleanSetting saveInventory = register(new BooleanSetting("SaveInventory", "Log inventory contents on death",   false));
    private final StringSetting  format        = register(new StringSetting ("Format",        "Log format", "Died at X:%x% Y:%y% Z:%z%"));

    public DeathCoords() {
        super("DeathCoords", "Shows coordinates on death", Category.QOL);
    }
}
