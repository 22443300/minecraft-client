package dev.phantom.client.modules.qol;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class AntiAFK extends Module {
    private final ModeSetting    mode     = register(new ModeSetting   ("Mode",     "Anti-AFK action type",           "Spin", new String[]{"Spin","Jump","Walk","Swing"}));
    private final IntSetting     delay    = register(new IntSetting    ("Delay",    "Ticks between actions",          200, 20, 1200));
    private final DoubleSetting  walkDist = register(new DoubleSetting ("WalkDist", "Distance to walk per cycle",     3.0, 1.0, 10.0, 0.5));
    private final BooleanSetting spin     = register(new BooleanSetting("Spin",     "Spin camera to prevent AFK kick",true));

    public AntiAFK() {
        super("AntiAFK", "Prevents AFK kick", Category.QOL);
    }
}
