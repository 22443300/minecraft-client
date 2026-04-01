package dev.phantom.client.modules.visual;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class NameTags extends Module {
    private final BooleanSetting health = register(new BooleanSetting("Health", "Show entity health", true));
    private final BooleanSetting distance = register(new BooleanSetting("Distance", "Show distance to entity", true));
    private final BooleanSetting armor = register(new BooleanSetting("Armor", "Show entity armor", true));
    private final BooleanSetting ping = register(new BooleanSetting("Ping", "Show player ping", false));
    private final BooleanSetting gameMode = register(new BooleanSetting("Game Mode", "Show player game mode", false));
    private final DoubleSetting scale = register(new DoubleSetting("Scale", "Nametag text scale", 1.0, 0.5, 3.0, 0.1));
    private final BooleanSetting background = register(new BooleanSetting("Background", "Draw background behind nametag", true));
    private final BooleanSetting armorBar = register(new BooleanSetting("Armor Bar", "Show armor durability bar", true));

    public NameTags() {
        super("NameTags", "Displays custom nametags on entities", Category.VISUAL);
    }
}
