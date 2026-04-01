package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class HotbarScroll extends Module {
    public static HotbarScroll INSTANCE;
    private final BooleanSetting reverse = register(new BooleanSetting("Reverse","Reverse scroll direction",false));
    private final ModeSetting mode = register(new ModeSetting("Mode","Scroll mode","Slot",new String[]{"Slot","Item","Stack"}));
    private final BooleanSetting wrap = register(new BooleanSetting("Wrap","Wrap around hotbar ends",true));
    public HotbarScroll() { super("HotbarScroll","Enhanced hotbar scrolling",Category.TWEAKEROO); INSTANCE=this; }
}
