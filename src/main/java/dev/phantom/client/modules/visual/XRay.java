package dev.phantom.client.modules.visual;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class XRay extends Module {
    private final BooleanSetting ores = register(new BooleanSetting("Ores", "Show all ores", true));
    private final BooleanSetting diamonds = register(new BooleanSetting("Diamonds", "Show diamond ore", true));
    private final BooleanSetting emeralds = register(new BooleanSetting("Emeralds", "Show emerald ore", false));
    private final BooleanSetting iron = register(new BooleanSetting("Iron", "Show iron ore", true));
    private final BooleanSetting gold = register(new BooleanSetting("Gold", "Show gold ore", true));
    private final BooleanSetting redstone = register(new BooleanSetting("Redstone", "Show redstone ore", true));
    private final BooleanSetting lapis = register(new BooleanSetting("Lapis", "Show lapis ore", false));
    private final BooleanSetting coal = register(new BooleanSetting("Coal", "Show coal ore", false));
    private final BooleanSetting ancient_debris = register(new BooleanSetting("Ancient Debris", "Show ancient debris", true));
    private final IntSetting opacity = register(new IntSetting("Opacity", "Opacity of xray rendering", 255, 50, 255));

    public static XRay INSTANCE;

    public XRay() {
        super("XRay", "See ores through walls", Category.VISUAL);
        INSTANCE = this;
    }
}
