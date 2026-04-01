package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class HandRestock extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode","Where to pull from","Hotbar",new String[]{"Hotbar","Inventory","Both"}));
    private final BooleanSetting preferStack = register(new BooleanSetting("PreferStack","Prefer largest stack",true));
    private final IntSetting delay = register(new IntSetting("Delay","Delay in ticks",5,0,20));
    private final IntSetting threshold = register(new IntSetting("Threshold","Restock below this count",16,1,64));
    public HandRestock() { super("HandRestock","Auto-restock held item",Category.TWEAKEROO); }
}
