package dev.phantom.client.modules.world;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class Nuker extends Module {
    private final DoubleSetting range = register(new DoubleSetting("Range", "Block break range", 4.0, 1.0, 8.0, 0.1));
    private final ModeSetting mode = register(new ModeSetting("Mode", "Nuker mode", "All", new String[]{"All", "ID", "Connected", "Flat", "Top"}));
    private final IntSetting delay = register(new IntSetting("Delay", "Delay between breaks in ticks", 0, 0, 20));
    private final BooleanSetting breakSelf = register(new BooleanSetting("BreakSelf", "Break the block the player is standing on", false));
    private final StringSetting blockId = register(new StringSetting("BlockId", "Block to target in ID mode", "minecraft:stone"));
    private final BooleanSetting rotate = register(new BooleanSetting("Rotate", "Rotate towards blocks before breaking", true));
    private final BooleanSetting swing = register(new BooleanSetting("Swing", "Swing arm animation when breaking", true));
    private final BooleanSetting packetMine = register(new BooleanSetting("PacketMine", "Use packet-based mining", false));

    public Nuker() {
        super("Nuker", "Breaks multiple blocks quickly", Category.WORLD);
    }
}
