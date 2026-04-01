package dev.phantom.client.modules.combat;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
import dev.phantom.client.core.event.EventHandler;
import dev.phantom.client.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class TPAura extends Module {
    private final DoubleSetting range = register(new DoubleSetting("Range", "Distance at which to target players", 100.0, 10.0, 500.0, 10.0));
    private final IntSetting delay = register(new IntSetting("Delay", "Delay between attacks in ticks", 5, 0, 50));
    private final ModeSetting mode = register(new ModeSetting("Mode", "Attack delivery method", "Packet", new String[]{"Packet", "Teleport"}));

    public TPAura() {
        super("TPAura", "Attacks players through long distances via packet manipulation", Category.COMBAT);
    }
}
