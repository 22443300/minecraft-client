package dev.phantom.client.modules.combat;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
import dev.phantom.client.core.event.EventHandler;
import dev.phantom.client.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class Criticals extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "Method used to trigger critical hits", "Packet", new String[]{"Packet", "Jump", "Hop", "Timer"}));
    private final BooleanSetting onlyInAura = register(new BooleanSetting("Only In Aura", "Only trigger criticals when KillAura is active", true));
    private final DoubleSetting minimumFallDist = register(new DoubleSetting("Min Fall Dist", "Minimum fall distance to count as critical", 0.0625, 0.0, 1.0, 0.01));

    public Criticals() {
        super("Criticals", "Ensures attacks are always critical hits", Category.COMBAT);
    }
}
