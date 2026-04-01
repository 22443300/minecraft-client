package dev.phantom.client.modules.combat;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
import dev.phantom.client.core.event.EventHandler;
import dev.phantom.client.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class SpearLunge extends Module {
    private final DoubleSetting force = register(new DoubleSetting("Force", "Lunge force applied during trident attack", 1.5, 0.5, 5.0, 0.1));
    private final BooleanSetting onAttack = register(new BooleanSetting("On Attack", "Trigger lunge automatically when attacking", true));

    public SpearLunge() {
        super("SpearLunge", "Automates trident lunge attacks", Category.COMBAT);
    }
}
