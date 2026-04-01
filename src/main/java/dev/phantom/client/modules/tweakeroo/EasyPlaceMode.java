package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class EasyPlaceMode extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode","Activation mode","Always",new String[]{"Always","Sneak","Hold"}));
    private final BooleanSetting whitelist = register(new BooleanSetting("Whitelist","Use block whitelist",false));
    private final BooleanSetting checkCollision = register(new BooleanSetting("CheckCollision","Check for entity collision",true));
    public EasyPlaceMode() { super("EasyPlaceMode","Place blocks on entities without sneaking",Category.TWEAKEROO); }
}
