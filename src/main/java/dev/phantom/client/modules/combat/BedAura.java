package dev.phantom.client.modules.combat;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
import dev.phantom.client.core.event.EventHandler;
import dev.phantom.client.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class BedAura extends Module {
    private final DoubleSetting range = register(new DoubleSetting("Range", "Range for placing and detonating beds", 5.0, 2.0, 7.0, 0.1));
    private final IntSetting placeDelay = register(new IntSetting("Place Delay", "Delay between bed placements in ticks", 2, 0, 20));
    private final IntSetting breakDelay = register(new IntSetting("Break Delay", "Delay between bed detonations in ticks", 2, 0, 20));
    private final DoubleSetting minDamage = register(new DoubleSetting("Min Damage", "Minimum damage required to detonate bed", 5.0, 1.0, 36.0, 0.5));
    private final DoubleSetting maxSelf = register(new DoubleSetting("Max Self", "Maximum self-damage allowed from bed explosion", 10.0, 1.0, 36.0, 0.5));

    public BedAura() {
        super("BedAura", "Automatically places and detonates beds near enemies", Category.COMBAT);
    }
}
