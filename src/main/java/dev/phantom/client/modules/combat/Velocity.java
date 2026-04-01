package dev.phantom.client.modules.combat;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
import dev.phantom.client.core.event.EventHandler;
import dev.phantom.client.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class Velocity extends Module {
    private final IntSetting horizontal = register(new IntSetting("Horizontal", "Horizontal knockback percentage to apply", 0, 0, 100));
    private final IntSetting vertical = register(new IntSetting("Vertical", "Vertical knockback percentage to apply", 0, 0, 100));
    private final ModeSetting mode = register(new ModeSetting("Mode", "Velocity modification method", "Cancel", new String[]{"Cancel", "Reduce", "Grim"}));
    private final BooleanSetting onlyOnFlag = register(new BooleanSetting("Only On Flag", "Only modify velocity when flagged by server", false));

    public Velocity() {
        super("Velocity", "Modifies knockback velocity received", Category.COMBAT);
    }
}
