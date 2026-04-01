package dev.phantom.client.modules.utility;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class MiddleClickFriend extends Module {
    private final BooleanSetting addOnMiddle = register(new BooleanSetting("Add On Middle", "Add player to list on middle click", true));
    private final BooleanSetting notification = register(new BooleanSetting("Notification", "Show notification when adding a player", true));
    private final ModeSetting relation = register(new ModeSetting("Relation", "Relation type to assign on middle click", "Friend", new String[]{"Friend", "Enemy"}));

    public MiddleClickFriend() {
        super("MiddleClickFriend", "Middle click to friend players", Category.UTILITY);
    }
}
