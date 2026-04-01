package dev.phantom.client.modules.utility;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class AutoEat extends Module {
    private final DoubleSetting healthThreshold = register(new DoubleSetting("Health Threshold", "Eat when health is below this value", 16.0, 1.0, 20.0, 0.5));
    private final IntSetting hungerThreshold = register(new IntSetting("Hunger Threshold", "Eat when hunger is below this value", 16, 1, 20));
    private final BooleanSetting preferBest = register(new BooleanSetting("Prefer Best", "Prefer the best food available", true));
    private final BooleanSetting eatGapple = register(new BooleanSetting("Eat Gapple", "Use golden apples for eating", false));
    private final IntSetting delay = register(new IntSetting("Delay", "Ticks between eat attempts", 0, 0, 20));
    private final BooleanSetting eatInCombat = register(new BooleanSetting("Eat In Combat", "Continue eating while in combat", true));

    public AutoEat() {
        super("AutoEat", "Automatically eats food when hungry or low health", Category.UTILITY);
    }
}
