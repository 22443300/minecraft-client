package dev.phantom.client.modules.qol;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class BetterF3 extends Module {
    private final BooleanSetting tps = register(new BooleanSetting("TPS","Show server TPS",true));
    private final BooleanSetting ping = register(new BooleanSetting("Ping","Show ping",true));
    private final BooleanSetting speed = register(new BooleanSetting("Speed","Show player speed",true));
    private final BooleanSetting direction = register(new BooleanSetting("Direction","Show facing direction",true));
    private final BooleanSetting biome = register(new BooleanSetting("Biome","Show current biome",true));
    private final BooleanSetting lightLevel = register(new BooleanSetting("LightLevel","Show light level",true));
    private final BooleanSetting chunkUpdates = register(new BooleanSetting("ChunkUpdates","Show chunk updates/sec",false));
    public BetterF3() { super("BetterF3","Adds extra info to the F3 debug screen",Category.QOL); }
}
