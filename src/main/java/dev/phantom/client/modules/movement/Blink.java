package dev.phantom.client.modules.movement;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class Blink extends Module {
    private final IntSetting maxPackets = register(new IntSetting("MaxPackets", "Maximum number of packets to buffer before auto-flushing", 200, 10, 1000));
    private final BooleanSetting cancelOnHit = register(new BooleanSetting("CancelOnHit", "Flush packets when you take damage", true));
    private final BooleanSetting showGhost = register(new BooleanSetting("ShowGhost", "Show ghost player at real position", true));
    private final ModeSetting mode = register(new ModeSetting("Mode", "Blink activation mode", "Hold", new String[]{"Hold", "Toggle", "Timer"}));

    public Blink() {
        super("Blink", "Delays sending position packets to the server", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
