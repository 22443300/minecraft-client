package dev.phantom.client.modules.utility;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class AutoPotion extends Module {
    private final BooleanSetting strength = register(new BooleanSetting("Strength", "Auto-drink strength potions", true));
    private final BooleanSetting speed = register(new BooleanSetting("Speed", "Auto-drink speed potions", false));
    private final BooleanSetting fire_resistance = register(new BooleanSetting("Fire Resistance", "Auto-drink fire resistance potions", true));
    private final BooleanSetting health_boost = register(new BooleanSetting("Health Boost", "Auto-drink health boost potions", false));
    private final IntSetting delay = register(new IntSetting("Delay", "Ticks between potion uses", 5, 0, 40));
    private final DoubleSetting threshold = register(new DoubleSetting("Threshold", "Drink health potions when health is below this", 15.0, 1.0, 20.0, 0.5));

    public AutoPotion() {
        super("AutoPotion", "Automatically drinks potions from inventory when needed", Category.UTILITY);
    }
}
