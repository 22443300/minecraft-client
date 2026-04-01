package dev.phantom.client.modules.qol;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class ItemPhysics extends Module {
    private final BooleanSetting rotation = register(new BooleanSetting("Rotation", "Enable rotation on dropped items",  true));
    private final DoubleSetting  velocity = register(new DoubleSetting ("Velocity", "Item drop velocity multiplier",     1.0, 0.1, 5.0, 0.1));
    private final DoubleSetting  size     = register(new DoubleSetting ("Size",     "Rendered size of dropped items",    0.25, 0.1, 1.0, 0.05));
    private final BooleanSetting spinning = register(new BooleanSetting("Spinning", "Items spin while on the ground",    true));
    private final BooleanSetting gravity  = register(new BooleanSetting("Gravity",  "Apply realistic gravity to items",  true));

    public ItemPhysics() {
        super("ItemPhysics", "Adds physics to dropped items", Category.QOL);
    }
}
