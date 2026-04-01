package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class SmartReplant extends Module {
    private final BooleanSetting wheat = register(new BooleanSetting("Wheat","Replant wheat",true));
    private final BooleanSetting carrot = register(new BooleanSetting("Carrot","Replant carrots",true));
    private final BooleanSetting potato = register(new BooleanSetting("Potato","Replant potatoes",true));
    private final BooleanSetting beetroot = register(new BooleanSetting("Beetroot","Replant beetroot",true));
    private final BooleanSetting sugarcane = register(new BooleanSetting("Sugarcane","Replant sugarcane",false));
    private final IntSetting delay = register(new IntSetting("Delay","Replant delay",2,0,20));
    public SmartReplant() { super("SmartReplant","Auto-replants crops after harvesting",Category.TWEAKEROO); }
}
