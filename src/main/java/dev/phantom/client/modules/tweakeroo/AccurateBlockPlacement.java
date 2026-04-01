package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class AccurateBlockPlacement extends Module {
    private final DoubleSetting range = register(new DoubleSetting("Range","Placement reach",5.0,3.0,8.0,0.1));
    private final BooleanSetting strict = register(new BooleanSetting("Strict","Strict placement check",true));
    private final BooleanSetting rotateTo = register(new BooleanSetting("RotateTo","Rotate to face before placing",true));
    private final IntSetting delay = register(new IntSetting("Delay","Delay before placing",1,0,10));
    public AccurateBlockPlacement() { super("AccurateBlockPlacement","More precise block placement",Category.TWEAKEROO); }
}
