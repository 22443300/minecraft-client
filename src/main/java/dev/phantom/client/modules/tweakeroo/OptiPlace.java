package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class OptiPlace extends Module {
    private final BooleanSetting packetPlace = register(new BooleanSetting("PacketPlace","Use packet placement",true));
    private final BooleanSetting swing = register(new BooleanSetting("Swing","Show swing animation",true));
    private final BooleanSetting multiPlace = register(new BooleanSetting("MultiPlace","Place multiple blocks per tick",false));
    private final IntSetting maxPerTick = register(new IntSetting("MaxPerTick","Max blocks per tick",4,1,20));
    public OptiPlace() { super("OptiPlace","Maximum speed block placement",Category.TWEAKEROO); }
}
