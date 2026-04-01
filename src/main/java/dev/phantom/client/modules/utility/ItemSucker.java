package dev.phantom.client.modules.utility;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class ItemSucker extends Module {
    private final DoubleSetting range = register(new DoubleSetting("Range", "Range to detect nearby items", 8.0, 2.0, 32.0, 1.0));
    private final ModeSetting filterMode = register(new ModeSetting("Filter Mode", "Item filter mode", "All", new String[]{"All", "Whitelist", "Blacklist"}));
    private final StringSetting items = register(new StringSetting("Items", "Item IDs to filter", ""));
    private final DoubleSetting magnetRange = register(new DoubleSetting("Magnet Range", "Range at which items are magnetically pulled", 2.0, 0.5, 8.0, 0.5));

    public ItemSucker() {
        super("ItemSucker", "Attracts nearby dropped items towards you", Category.UTILITY);
    }
}
