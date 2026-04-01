package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class LargerInventory extends Module {
    private final IntSetting rows = register(new IntSetting("Rows","Visible inventory rows",4,3,6));
    private final IntSetting columns = register(new IntSetting("Columns","Inventory columns",9,9,18));
    private final BooleanSetting smoothScroll = register(new BooleanSetting("SmoothScroll","Enable smooth scrolling",true));
    public LargerInventory() { super("LargerInventory","Expands the inventory GUI",Category.TWEAKEROO); }
}
