package dev.phantom.client.modules.utility;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class NoInteract extends Module {
    private final BooleanSetting entities = register(new BooleanSetting("Entities", "Prevent interacting with entities", true));
    private final BooleanSetting blocks = register(new BooleanSetting("Blocks", "Prevent interacting with blocks", false));
    private final BooleanSetting items = register(new BooleanSetting("Items", "Prevent picking up items", false));
    private final BooleanSetting vehicles = register(new BooleanSetting("Vehicles", "Prevent mounting vehicles", true));

    public NoInteract() {
        super("NoInteract", "Prevents accidental interactions", Category.UTILITY);
    }
}
