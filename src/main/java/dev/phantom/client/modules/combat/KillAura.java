package dev.phantom.client.modules.combat;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
import dev.phantom.client.core.event.EventHandler;
import dev.phantom.client.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class KillAura extends Module {
    private final DoubleSetting range = register(new DoubleSetting("Range", "Attack range", 4.0, 1.0, 6.0, 0.1));
    private final BooleanSetting targetPlayers = register(new BooleanSetting("Target Players", "Target player entities", true));
    private final BooleanSetting targetMobs = register(new BooleanSetting("Target Mobs", "Target hostile mobs", true));
    private final BooleanSetting targetAnimals = register(new BooleanSetting("Target Animals", "Target passive animals", false));
    private final BooleanSetting rotations = register(new BooleanSetting("Rotations", "Rotate toward target before attacking", true));
    private final ModeSetting sort = register(new ModeSetting("Sort", "Target sorting method", "Nearest", new String[]{"Nearest", "Health", "Angle"}));
    private final IntSetting swingDelay = register(new IntSetting("Swing Delay", "Delay between swings in ticks", 0, 0, 20));
    private final BooleanSetting throughWalls = register(new BooleanSetting("Through Walls", "Attack targets through walls", false));

    public KillAura() {
        super("KillAura", "Automatically attacks nearby entities", Category.COMBAT);
    }
}
