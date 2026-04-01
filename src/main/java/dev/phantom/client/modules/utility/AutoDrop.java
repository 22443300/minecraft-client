package dev.phantom.client.modules.utility;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class AutoDrop extends Module {
    private final BooleanSetting hotbar = register(new BooleanSetting("Hotbar", "Drop items from hotbar", false));
    private final BooleanSetting inventory = register(new BooleanSetting("Inventory", "Drop items from main inventory", true));
    private final StringSetting itemFilter = register(new StringSetting("Item Filter", "Comma-separated item IDs to drop", ""));
    private final ModeSetting mode = register(new ModeSetting("Mode", "Filter mode for dropping items", "Blacklist", new String[]{"Blacklist", "Whitelist"}));
    private final BooleanSetting onPickup = register(new BooleanSetting("On Pickup", "Drop items immediately on pickup", true));

    public AutoDrop() {
        super("AutoDrop", "Automatically drops specified items", Category.UTILITY);
    }
}
