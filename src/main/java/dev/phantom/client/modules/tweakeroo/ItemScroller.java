package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class ItemScroller extends Module {
    private final IntSetting amount = register(new IntSetting("Amount","Items per scroll",1,1,64));
    private final BooleanSetting smooth = register(new BooleanSetting("Smooth","Smooth scroll animation",false));
    private final BooleanSetting reverse = register(new BooleanSetting("Reverse","Reverse scroll direction",false));
    private final IntSetting shiftMultiplier = register(new IntSetting("ShiftMultiplier","Multiplier when holding Shift",8,2,64));
    public ItemScroller() { super("ItemScroller","Scroll to move items in inventories",Category.TWEAKEROO); }
}
