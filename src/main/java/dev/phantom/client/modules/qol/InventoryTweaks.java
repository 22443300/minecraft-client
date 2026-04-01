package dev.phantom.client.modules.qol;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class InventoryTweaks extends Module {
    private final BooleanSetting autoSort       = register(new BooleanSetting("AutoSort",      "Automatically sort inventory",         false));
    private final BooleanSetting shiftClick     = register(new BooleanSetting("ShiftClick",    "Enable shift-click quick move",        true));
    private final BooleanSetting autoRefill     = register(new BooleanSetting("AutoRefill",    "Auto-refill depleted item stacks",     true));
    private final BooleanSetting autoDropJunk   = register(new BooleanSetting("AutoDropJunk",  "Automatically drop junk items",        false));
    private final BooleanSetting closeOnEsc     = register(new BooleanSetting("CloseOnEsc",    "Close inventory on Escape",            true));
    private final BooleanSetting middleClickDrop= register(new BooleanSetting("MiddleClickDrop","Drop item with middle click",         false));

    public InventoryTweaks() {
        super("InventoryTweaks", "Adds inventory quality of life improvements", Category.QOL);
    }
}
