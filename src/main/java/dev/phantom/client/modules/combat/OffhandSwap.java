package dev.phantom.client.modules.combat;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
import dev.phantom.client.core.event.EventHandler;
import dev.phantom.client.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class OffhandSwap extends Module {
    private final ModeSetting item = register(new ModeSetting("Item", "Item to keep in offhand", "Totem", new String[]{"Totem", "Crystal", "Gapple", "Shield", "Arrow"}));
    private final IntSetting swapBackDelay = register(new IntSetting("Swap Back Delay", "Delay before swapping back to original item in ticks", 5, 0, 40));
    private final BooleanSetting keepOnDeath = register(new BooleanSetting("Keep On Death", "Attempt to keep offhand item on death", false));

    public OffhandSwap() {
        super("OffhandSwap", "Quickly swaps items to/from offhand", Category.COMBAT);
    }
}
