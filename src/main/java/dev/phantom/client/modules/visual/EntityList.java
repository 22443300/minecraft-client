package dev.phantom.client.modules.visual;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class EntityList extends Module {
    private final BooleanSetting players = register(new BooleanSetting("Players", "Show player count", true));
    private final BooleanSetting mobs = register(new BooleanSetting("Mobs", "Show mob count", true));
    private final BooleanSetting animals = register(new BooleanSetting("Animals", "Show animal count", false));
    private final BooleanSetting items = register(new BooleanSetting("Items", "Show item count", false));
    private final ModeSetting position = register(new ModeSetting("Position", "HUD position", "TopRight", new String[]{"TopRight", "TopLeft", "BottomRight", "BottomLeft"}));

    public EntityList() {
        super("EntityList", "Shows a list of nearby entities and their count on the HUD", Category.VISUAL);
    }
}
