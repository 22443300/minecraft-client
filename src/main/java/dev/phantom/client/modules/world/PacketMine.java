package dev.phantom.client.modules.world;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class PacketMine extends Module {
    public static PacketMine INSTANCE;

    private final DoubleSetting range = register(new DoubleSetting("Range", "Maximum block break range", 4.5, 2.0, 6.0, 0.1));
    private final BooleanSetting swing = register(new BooleanSetting("Swing", "Swing arm animation when mining", true));
    private final BooleanSetting rotate = register(new BooleanSetting("Rotate", "Rotate towards target block", true));
    private final IntSetting delay = register(new IntSetting("Delay", "Delay between packet mine actions in ticks", 0, 0, 5));

    public PacketMine() {
        super("PacketMine", "Mines blocks instantly using packet manipulation", Category.WORLD);
        INSTANCE = this;
    }
}
