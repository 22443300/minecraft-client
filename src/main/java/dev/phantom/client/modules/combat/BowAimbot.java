package dev.phantom.client.modules.combat;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
import dev.phantom.client.core.event.EventHandler;
import dev.phantom.client.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class BowAimbot extends Module {
    private final DoubleSetting range = register(new DoubleSetting("Range", "Target acquisition range for bow", 20.0, 5.0, 60.0, 1.0));
    private final DoubleSetting aimSpeed = register(new DoubleSetting("Aim Speed", "Speed of aim correction", 5.0, 0.5, 20.0, 0.5));
    private final BooleanSetting targetPlayers = register(new BooleanSetting("Target Players", "Aim at player entities", true));
    private final BooleanSetting targetMobs = register(new BooleanSetting("Target Mobs", "Aim at hostile mobs", false));
    private final BooleanSetting autoShoot = register(new BooleanSetting("Auto Shoot", "Automatically release bow when aimed", false));

    public BowAimbot() {
        super("BowAimbot", "Automatically aims your bow", Category.COMBAT);
    }
}
