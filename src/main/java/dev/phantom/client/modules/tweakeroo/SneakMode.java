package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class SneakMode extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode","Sneak behavior","Hold",new String[]{"Hold","Toggle","Auto","Crawl"}));
    private final BooleanSetting autoOnEdge = register(new BooleanSetting("AutoOnEdge","Sneak near edges",false));
    private final BooleanSetting autoInCombat = register(new BooleanSetting("AutoInCombat","Sneak when taking damage",false));
    public SneakMode() { super("SneakMode","Configurable sneak behavior",Category.TWEAKEROO); }
}
