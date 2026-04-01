package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class AutoClutch extends Module {
    private final BooleanSetting water = register(new BooleanSetting("Water","Use water bucket clutch",true));
    private final BooleanSetting block = register(new BooleanSetting("Block","Use block clutch",true));
    private final BooleanSetting mlg = register(new BooleanSetting("MLG","Enable MLG water",true));
    private final DoubleSetting height = register(new DoubleSetting("Height","Clutch activation height",6.0,2.0,20.0,0.5));
    private final IntSetting delay = register(new IntSetting("Delay","Delay in ticks",0,0,5));
    public AutoClutch() { super("AutoClutch","Automatically MLG/clutch when falling",Category.TWEAKEROO); }
}
