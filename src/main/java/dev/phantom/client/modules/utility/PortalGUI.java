package dev.phantom.client.modules.utility;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class PortalGUI extends Module {
    private final BooleanSetting allowNether = register(new BooleanSetting("Allow Nether", "Allow GUI interaction inside nether portals", true));
    private final BooleanSetting allowEnd = register(new BooleanSetting("Allow End", "Allow GUI interaction inside end portals", true));
    private final BooleanSetting preventEntry = register(new BooleanSetting("Prevent Entry", "Prevent entering portals entirely", false));

    public PortalGUI() {
        super("PortalGUI", "Allows opening GUIs and inventories inside portals", Category.UTILITY);
    }
}
