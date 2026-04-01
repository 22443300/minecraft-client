package dev.phantom.client.modules.movement;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class ElytraSwap extends Module {
    private final BooleanSetting chestplateSwap = register(new BooleanSetting("ChestplateSwap", "Swap to chestplate when landing", true));
    private final IntSetting durabilityThreshold = register(new IntSetting("DurabilityThreshold", "Swap when elytra durability falls below this", 20, 1, 100));
    private final IntSetting swapDelay = register(new IntSetting("SwapDelay", "Delay in ticks before swapping", 3, 0, 20));

    public ElytraSwap() {
        super("ElytraSwap", "Automatically swaps elytra and chestplate", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
