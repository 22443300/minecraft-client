package dev.phantom.client.modules.visual;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class Trajectories extends Module {
    private final IntSetting bounces = register(new IntSetting("Bounces", "Number of trajectory bounces to simulate", 3, 1, 20));
    private final IntSetting trailLength = register(new IntSetting("Trail Length", "Number of trajectory points to draw", 100, 10, 500));
    private final ColorSetting color = register(new ColorSetting("Color", "Trajectory line color", 1.0f, 1.0f, 0.0f, 0.8f));
    private final DoubleSetting thickness = register(new DoubleSetting("Thickness", "Trajectory line thickness", 1.5, 0.5, 5.0, 0.1));
    private final BooleanSetting arrows = register(new BooleanSetting("Arrows", "Show trajectory for arrows", true));
    private final BooleanSetting throwables = register(new BooleanSetting("Throwables", "Show trajectory for throwables", true));
    private final BooleanSetting trident = register(new BooleanSetting("Trident", "Show trajectory for tridents", true));

    public Trajectories() {
        super("Trajectories", "Shows projectile trajectories", Category.VISUAL);
    }
}
