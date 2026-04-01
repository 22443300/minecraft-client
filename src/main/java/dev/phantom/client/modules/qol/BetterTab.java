package dev.phantom.client.modules.qol;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class BetterTab extends Module {
    private final BooleanSetting showSelf  = register(new BooleanSetting("ShowSelf",  "Show yourself in the tab list",        true));
    private final BooleanSetting ping      = register(new BooleanSetting("Ping",      "Show ping in tab list",                true));
    private final BooleanSetting gameMode  = register(new BooleanSetting("GameMode",  "Show game mode in tab list",           false));
    private final BooleanSetting headLevel = register(new BooleanSetting("HeadLevel", "Show player head level in tab list",   false));
    private final ModeSetting    sortMode  = register(new ModeSetting   ("SortMode",  "Tab list sort order",                  "Name", new String[]{"Name","Ping","GameMode"}));
    private final IntSetting     maxPlayers= register(new IntSetting    ("MaxPlayers","Maximum players shown in tab list",    80, 10, 1000));

    public BetterTab() {
        super("BetterTab", "Enhances the tab list display", Category.QOL);
    }
}
