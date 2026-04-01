package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class StackRefill extends Module {
    private final IntSetting threshold = register(new IntSetting("Threshold","Refill when below this count",16,1,32));
    private final BooleanSetting preferSameSlot = register(new BooleanSetting("PreferSameSlot","Prefer same hotbar slot",true));
    private final IntSetting delay = register(new IntSetting("Delay","Delay in ticks",3,0,20));
    private final BooleanSetting hotbarOnly = register(new BooleanSetting("HotbarOnly","Only refill hotbar",false));
    public StackRefill() { super("StackRefill","Refills stacks from inventory automatically",Category.TWEAKEROO); }
}
