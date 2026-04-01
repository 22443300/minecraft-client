package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class RedstoneHelper extends Module {
    private final BooleanSetting showPower = register(new BooleanSetting("ShowPower","Show redstone power levels",true));
    private final BooleanSetting showRepeaters = register(new BooleanSetting("ShowRepeaters","Show repeater delays",true));
    private final BooleanSetting showComparators = register(new BooleanSetting("ShowComparators","Show comparator signal",true));
    private final BooleanSetting showStrength = register(new BooleanSetting("ShowStrength","Show signal strength",false));
    private final ColorSetting textColor = register(new ColorSetting("TextColor","Label color",1.0f,0.3f,0.1f,1.0f));
    public RedstoneHelper() { super("RedstoneHelper","Visualises redstone signal strengths",Category.TWEAKEROO); }
}
