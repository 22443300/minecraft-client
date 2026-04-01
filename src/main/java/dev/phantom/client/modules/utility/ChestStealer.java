package dev.phantom.client.modules.utility;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class ChestStealer extends Module {
    private final IntSetting delay = register(new IntSetting("Delay", "Ticks between item steals", 5, 0, 20));
    private final BooleanSetting takeAll = register(new BooleanSetting("Take All", "Take all items from the chest", true));
    private final BooleanSetting closeWhenDone = register(new BooleanSetting("Close When Done", "Close the chest when finished stealing", true));
    private final BooleanSetting sort = register(new BooleanSetting("Sort", "Sort items before taking", false));
    private final ModeSetting filterMode = register(new ModeSetting("Filter Mode", "Item filter mode", "All", new String[]{"All", "Blacklist", "Whitelist"}));
    private final StringSetting filterItems = register(new StringSetting("Filter Items", "Comma-separated item IDs", ""));

    public ChestStealer() {
        super("ChestStealer", "Steals items from nearby chests", Category.UTILITY);
    }
}
