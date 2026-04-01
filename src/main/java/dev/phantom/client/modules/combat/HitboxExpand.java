package dev.phantom.client.modules.combat;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
import dev.phantom.client.core.event.EventHandler;
import dev.phantom.client.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class HitboxExpand extends Module {
    public static HitboxExpand INSTANCE;

    private final DoubleSetting expansion = register(new DoubleSetting("Expansion", "Amount to expand entity hitboxes by", 0.1, 0.0, 1.0, 0.05));
    private final BooleanSetting players = register(new BooleanSetting("Players", "Expand hitboxes for player entities", true));
    private final BooleanSetting mobs = register(new BooleanSetting("Mobs", "Expand hitboxes for mob entities", true));

    public HitboxExpand() {
        super("HitboxExpand", "Expands entity hitboxes to make hitting easier", Category.COMBAT);
        INSTANCE = this;
    }
}
