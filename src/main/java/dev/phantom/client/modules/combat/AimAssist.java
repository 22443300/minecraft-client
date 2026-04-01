package dev.phantom.client.modules.combat;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
import dev.phantom.client.core.event.EventHandler;
import dev.phantom.client.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class AimAssist extends Module {
    private final DoubleSetting range = register(new DoubleSetting("Range", "Target acquisition range", 5.0, 1.0, 10.0, 0.1));
    private final DoubleSetting fov = register(new DoubleSetting("FOV", "Field of view for target detection in degrees", 90.0, 10.0, 180.0, 5.0));
    private final DoubleSetting speed = register(new DoubleSetting("Speed", "Aim assist rotation speed", 2.0, 0.1, 10.0, 0.1));
    private final BooleanSetting targetPlayers = register(new BooleanSetting("Target Players", "Assist aiming at player entities", true));
    private final BooleanSetting targetMobs = register(new BooleanSetting("Target Mobs", "Assist aiming at hostile mobs", false));
    private final BooleanSetting targetAnimals = register(new BooleanSetting("Target Animals", "Assist aiming at passive animals", false));
    private final BooleanSetting lockOn = register(new BooleanSetting("Lock On", "Fully lock onto target", false));

    public AimAssist() {
        super("AimAssist", "Assists with aiming at nearby entities", Category.COMBAT);
    }
}
