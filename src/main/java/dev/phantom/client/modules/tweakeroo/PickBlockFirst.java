package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class PickBlockFirst extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode","Pick block mode","Creative",new String[]{"Creative","Survival"}));
    private final BooleanSetting copyNBT = register(new BooleanSetting("CopyNBT","Copy NBT data",true));
    private final BooleanSetting preferHotbar = register(new BooleanSetting("PreferHotbar","Prefer hotbar slots",true));
    public PickBlockFirst() { super("PickBlockFirst","Pick block before breaking",Category.TWEAKEROO); }
}
