package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class AutoSneak extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode","When to sneak","Always",new String[]{"Always","Moving","Edges","Hold"}));
    private final BooleanSetting onLadder = register(new BooleanSetting("OnLadder","Auto-sneak on ladders",false));
    public AutoSneak() { super("AutoSneak","Automatically sneaks for you",Category.TWEAKEROO); }
}
