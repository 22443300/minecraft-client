package dev.phantom.client.modules.world;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class TreeCapitator extends Module {
    private final IntSetting range = register(new IntSetting("Range", "Search range for connected logs", 7, 3, 32));
    private final BooleanSetting breakLeaves = register(new BooleanSetting("BreakLeaves", "Also break leaf blocks connected to the tree", true));
    private final BooleanSetting onlyWithAxe = register(new BooleanSetting("OnlyWithAxe", "Only activate when holding an axe", true));
    private final IntSetting delay = register(new IntSetting("Delay", "Delay between breaking each log in ticks", 2, 0, 20));
    private final IntSetting limit = register(new IntSetting("Limit", "Maximum number of logs to break per tree", 150, 10, 500));

    public TreeCapitator() {
        super("TreeCapitator", "Breaks entire trees at once", Category.WORLD);
    }
}
