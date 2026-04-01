package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class FastBlockPlacement extends Module {
    private final IntSetting delay = register(new IntSetting("Delay","Ticks between placements",0,0,10));
    private final BooleanSetting multiBlock = register(new BooleanSetting("MultiBlock","Place multiple per tick",false));
    private final BooleanSetting packetPlace = register(new BooleanSetting("PacketPlace","Use packet placement",true));
    private final BooleanSetting swing = register(new BooleanSetting("Swing","Swing hand on place",true));
    public FastBlockPlacement() { super("FastBlockPlacement","Place blocks faster",Category.TWEAKEROO); }
}
