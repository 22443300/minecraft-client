package dev.phantom.client.modules.utility;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class AutoRespawn extends Module {
    private final IntSetting delay = register(new IntSetting("Delay", "Ticks to wait before respawning", 0, 0, 20));
    private final BooleanSetting confirm = register(new BooleanSetting("Confirm", "Show confirmation before respawning", false));
    private final BooleanSetting returnToSpawn = register(new BooleanSetting("Return To Spawn", "Walk back to spawn point after respawn", false));

    public AutoRespawn() {
        super("AutoRespawn", "Automatically respawns on death", Category.UTILITY);
    }
}
