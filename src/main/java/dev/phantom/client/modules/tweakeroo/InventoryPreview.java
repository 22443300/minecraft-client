package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class InventoryPreview extends Module {
    private final DoubleSetting scale = register(new DoubleSetting("Scale","Preview scale",1.0,0.5,2.0,0.1));
    private final IntSetting maxRows = register(new IntSetting("MaxRows","Max rows to show",6,3,9));
    private final BooleanSetting showTooltips = register(new BooleanSetting("ShowTooltips","Show item tooltips",true));
    private final BooleanSetting showOnHover = register(new BooleanSetting("ShowOnHover","Only show when hovering",true));
    public InventoryPreview() { super("InventoryPreview","Preview container contents on hover",Category.TWEAKEROO); }
}
