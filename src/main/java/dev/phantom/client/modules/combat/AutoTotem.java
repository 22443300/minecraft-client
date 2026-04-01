package dev.phantom.client.modules.combat;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
import dev.phantom.client.core.event.EventHandler;
import dev.phantom.client.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class AutoTotem extends Module {
    private final DoubleSetting healthThreshold = register(new DoubleSetting("Health Threshold", "Health level at which to prioritize totem", 14.0, 1.0, 20.0, 0.5));
    private final BooleanSetting checkOffhand = register(new BooleanSetting("Check Offhand", "Verify offhand slot contains totem", true));
    private final BooleanSetting preferGapple = register(new BooleanSetting("Prefer Gapple", "Prefer golden apple over totem when health is high", false));
    private final IntSetting delay = register(new IntSetting("Delay", "Delay between totem swap checks in ticks", 0, 0, 10));

    public AutoTotem() {
        super("AutoTotem", "Automatically places totems in offhand", Category.COMBAT);
    }
}
