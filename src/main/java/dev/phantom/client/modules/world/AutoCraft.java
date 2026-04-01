package dev.phantom.client.modules.world;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class AutoCraft extends Module {
    private final ModeSetting recipe = register(new ModeSetting("Recipe", "Recipe to craft", "Custom", new String[]{"Custom", "Sticks", "Torch", "Planks"}));
    private final IntSetting amount = register(new IntSetting("Amount", "Number of items to craft", 64, 1, 1728));
    private final IntSetting delay = register(new IntSetting("Delay", "Delay between crafting actions in ticks", 5, 1, 20));
    private final BooleanSetting closWhenDone = register(new BooleanSetting("CloseWhenDone", "Close the crafting table when finished", true));

    public AutoCraft() {
        super("AutoCraft", "Automatically crafts items in a crafting table", Category.WORLD);
    }
}
