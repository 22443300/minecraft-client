package dev.phantom.client.modules.visual;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class ESP extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "ESP render mode", "Box", new String[]{"Box", "Outline", "Corner", "Glow", "Wireframe"}));
    private final BooleanSetting players = register(new BooleanSetting("Players", "Show player ESP", true));
    private final BooleanSetting mobs = register(new BooleanSetting("Mobs", "Show mob ESP", true));
    private final BooleanSetting animals = register(new BooleanSetting("Animals", "Show animal ESP", false));
    private final BooleanSetting items = register(new BooleanSetting("Items", "Show item ESP", false));
    private final DoubleSetting range = register(new DoubleSetting("Range", "ESP render range", 64.0, 8.0, 256.0, 8.0));
    private final BooleanSetting tracers = register(new BooleanSetting("Tracers", "Draw tracers to entities", false));
    private final BooleanSetting throughWalls = register(new BooleanSetting("Through Walls", "Show ESP through walls", true));
    private final ColorSetting playerColor = register(new ColorSetting("Player Color", "Color for player ESP", 0.2f, 0.6f, 1.0f, 1.0f));
    private final ColorSetting mobColor = register(new ColorSetting("Mob Color", "Color for mob ESP", 1.0f, 0.3f, 0.3f, 1.0f));

    public ESP() {
        super("ESP", "Highlights entities through walls", Category.VISUAL);
    }
}
