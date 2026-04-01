package dev.phantom.client.modules.visual;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class Tracers extends Module {
    private final BooleanSetting players = register(new BooleanSetting("Players", "Draw tracers to players", true));
    private final BooleanSetting mobs = register(new BooleanSetting("Mobs", "Draw tracers to mobs", true));
    private final BooleanSetting animals = register(new BooleanSetting("Animals", "Draw tracers to animals", false));
    private final BooleanSetting items = register(new BooleanSetting("Items", "Draw tracers to items", false));
    private final DoubleSetting thickness = register(new DoubleSetting("Thickness", "Tracer line thickness", 1.0, 0.5, 4.0, 0.1));
    private final ModeSetting mode = register(new ModeSetting("Mode", "Tracer origin point", "Eyes", new String[]{"Eyes", "Feet", "Middle"}));
    private final ColorSetting playerColor = register(new ColorSetting("Player Color", "Color for player tracers", 0.2f, 0.6f, 1.0f, 1.0f));
    private final ColorSetting mobColor = register(new ColorSetting("Mob Color", "Color for mob tracers", 1.0f, 0.3f, 0.3f, 1.0f));

    public Tracers() {
        super("Tracers", "Draws lines to nearby entities", Category.VISUAL);
    }
}
