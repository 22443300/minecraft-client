package dev.phantom.client.modules.world;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class AutoSmelter extends Module {
    private final BooleanSetting autoFuel = register(new BooleanSetting("AutoFuel", "Automatically insert fuel into the furnace", true));
    private final ModeSetting fuelType = register(new ModeSetting("FuelType", "Preferred fuel type to use", "Coal", new String[]{"Coal", "Wood", "Lava", "Any"}));
    private final BooleanSetting checkFull = register(new BooleanSetting("CheckFull", "Stop smelting when output is full", true));
    private final BooleanSetting pullOutput = register(new BooleanSetting("PullOutput", "Automatically pull output items from the furnace", true));

    public AutoSmelter() {
        super("AutoSmelter", "Automates smelting in furnaces", Category.WORLD);
    }
}
