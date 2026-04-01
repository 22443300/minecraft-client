package dev.phantom.client.modules.combat;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
import dev.phantom.client.core.event.EventHandler;
import dev.phantom.client.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class Reach extends Module {
    public static Reach INSTANCE;

    private final DoubleSetting reach = register(new DoubleSetting("Reach", "Entity attack reach distance", 4.5, 3.0, 6.0, 0.1));
    private final DoubleSetting blockReach = register(new DoubleSetting("Block Reach", "Block interaction reach distance", 5.0, 4.5, 6.0, 0.1));
    private final BooleanSetting onlyOnPlayers = register(new BooleanSetting("Only On Players", "Only extend reach when targeting players", false));

    public Reach() {
        super("Reach", "Extends player attack and block interaction range", Category.COMBAT);
        INSTANCE = this;
    }
}
