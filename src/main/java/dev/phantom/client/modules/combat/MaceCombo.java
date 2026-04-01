package dev.phantom.client.modules.combat;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
import dev.phantom.client.core.event.EventHandler;
import dev.phantom.client.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class MaceCombo extends Module {
    private final DoubleSetting height = register(new DoubleSetting("Height", "Height to ascend before smashing down with mace", 4.0, 1.0, 20.0, 0.5));
    private final ModeSetting mode = register(new ModeSetting("Mode", "Mace combo execution mode", "Auto", new String[]{"Auto", "Manual"}));
    private final IntSetting delay = register(new IntSetting("Delay", "Delay between combo steps in ticks", 2, 0, 10));
    private final BooleanSetting onlyOnPlayer = register(new BooleanSetting("Only On Player", "Only perform mace combo against players", true));

    public MaceCombo() {
        super("MaceCombo", "Automates mace attack combos", Category.COMBAT);
    }
}
