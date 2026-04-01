package dev.phantom.client.modules.combat;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
import dev.phantom.client.core.event.EventHandler;
import dev.phantom.client.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class AutoGapple extends Module {
    private final DoubleSetting healthThreshold = register(new DoubleSetting("Health Threshold", "Health level at which to eat a golden apple", 16.0, 1.0, 20.0, 0.5));
    private final BooleanSetting useNotchApple = register(new BooleanSetting("Use Notch Apple", "Use enchanted golden apple if available", false));
    private final IntSetting eatDelay = register(new IntSetting("Eat Delay", "Delay between golden apple uses in ticks", 0, 0, 20));
    private final BooleanSetting onlyInCombat = register(new BooleanSetting("Only In Combat", "Only eat golden apples while in combat", false));

    public AutoGapple() {
        super("AutoGapple", "Automatically eats golden apples when health is low", Category.COMBAT);
    }
}
