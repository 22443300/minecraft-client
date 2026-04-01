package dev.phantom.client.modules.combat;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
import dev.phantom.client.core.event.EventHandler;
import dev.phantom.client.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class AnchorAura extends Module {
    private final DoubleSetting range = register(new DoubleSetting("Range", "Range for anchor placement and activation", 4.0, 2.0, 6.0, 0.1));
    private final BooleanSetting autoSwitch = register(new BooleanSetting("Auto Switch", "Automatically switch to respawn anchor", true));
    private final IntSetting placeDelay = register(new IntSetting("Place Delay", "Delay between anchor placements in ticks", 3, 0, 20));

    public AnchorAura() {
        super("AnchorAura", "Automatically activates respawn anchors near enemies", Category.COMBAT);
    }
}
