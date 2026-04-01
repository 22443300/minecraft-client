package dev.phantom.client.modules.visual;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class PlayerESP extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "ESP render mode", "Box", new String[]{"Box", "Outline", "Glow", "Corner"}));
    private final BooleanSetting showFriends = register(new BooleanSetting("Show Friends", "Show ESP for friends", false));
    private final BooleanSetting showEnemies = register(new BooleanSetting("Show Enemies", "Show ESP for enemies", true));
    private final BooleanSetting showSelf = register(new BooleanSetting("Show Self", "Show ESP for yourself", false));
    private final ColorSetting friendColor = register(new ColorSetting("Friend Color", "Color for friends", 0.0f, 1.0f, 0.4f, 1.0f));
    private final ColorSetting enemyColor = register(new ColorSetting("Enemy Color", "Color for enemies", 1.0f, 0.2f, 0.2f, 1.0f));
    private final IntSetting fillOpacity = register(new IntSetting("Fill Opacity", "Opacity of ESP fill", 20, 0, 255));
    private final DoubleSetting outlineWidth = register(new DoubleSetting("Outline Width", "Width of ESP outline", 1.5, 0.5, 4.0, 0.5));

    public PlayerESP() {
        super("PlayerESP", "Dedicated ESP for player entities with friend/enemy coloring", Category.VISUAL);
    }
}
