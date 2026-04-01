package dev.phantom.client.modules.combat;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
import dev.phantom.client.core.event.EventHandler;
import dev.phantom.client.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class Surround extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "Block material to use for surrounding", "Obsidian", new String[]{"Obsidian", "Bedrock", "Any"}));
    private final BooleanSetting toggleOnDamage = register(new BooleanSetting("Toggle On Damage", "Re-enable surround when taking damage", false));
    private final BooleanSetting center = register(new BooleanSetting("Center", "Center player on block before placing", true));
    private final IntSetting delay = register(new IntSetting("Delay", "Delay between block placements in ticks", 0, 0, 10));

    public Surround() {
        super("Surround", "Surrounds you with obsidian for protection", Category.COMBAT);
    }
}
