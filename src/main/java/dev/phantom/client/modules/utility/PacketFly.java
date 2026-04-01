package dev.phantom.client.modules.utility;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class PacketFly extends Module {
    private final DoubleSetting speed = register(new DoubleSetting("Speed", "Flight speed multiplier", 1.0, 0.1, 10.0, 0.1));
    private final BooleanSetting vertical = register(new BooleanSetting("Vertical", "Allow vertical movement", true));
    private final BooleanSetting noCheatMode = register(new BooleanSetting("No Cheat Mode", "Use anti-cheat bypass techniques", false));
    private final IntSetting tickRate = register(new IntSetting("Tick Rate", "Packet send rate per tick", 4, 1, 20));

    public PacketFly() {
        super("PacketFly", "Fly using packet manipulation", Category.UTILITY);
    }
}
