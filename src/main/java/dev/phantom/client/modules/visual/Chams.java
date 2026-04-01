package dev.phantom.client.modules.visual;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class Chams extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "Chams render mode", "Flat", new String[]{"Flat", "Wireframe", "Model", "Glow"}));
    private final BooleanSetting players = register(new BooleanSetting("Players", "Apply chams to players", true));
    private final BooleanSetting mobs = register(new BooleanSetting("Mobs", "Apply chams to mobs", false));
    private final BooleanSetting throughWalls = register(new BooleanSetting("Through Walls", "Show chams through walls", true));
    private final ColorSetting visible = register(new ColorSetting("Visible", "Color when entity is visible", 0.2f, 0.6f, 1.0f, 0.8f));
    private final ColorSetting hidden = register(new ColorSetting("Hidden", "Color when entity is behind wall", 1.0f, 0.2f, 0.2f, 0.6f));

    public static Chams INSTANCE;

    public Chams() {
        super("Chams", "Renders entities with custom shaders", Category.VISUAL);
        INSTANCE = this;
    }
}
