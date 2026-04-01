package dev.phantom.client.modules.qol;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class CoordLogger extends Module {
    private final StringSetting  format    = register(new StringSetting ("Format",    "Log format", "[%name%] X:%x% Y:%y% Z:%z%"));
    private final BooleanSetting logToFile = register(new BooleanSetting("LogToFile", "Save coordinates to a log file",    true));
    private final BooleanSetting announce  = register(new BooleanSetting("Announce",  "Broadcast coordinates in chat",     false));
    private final BooleanSetting dimension = register(new BooleanSetting("Dimension", "Include dimension in log entry",    true));

    public CoordLogger() {
        super("CoordLogger", "Logs coordinates to a file", Category.QOL);
    }
}
