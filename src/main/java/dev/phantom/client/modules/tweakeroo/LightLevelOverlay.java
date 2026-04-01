package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class LightLevelOverlay extends Module {
    private final IntSetting threshold = register(new IntSetting("Threshold","Unsafe light level",8,0,15));
    private final ColorSetting safeColor = register(new ColorSetting("SafeColor","Safe light color",0.0f,1.0f,0.0f,0.8f));
    private final ColorSetting unsafeColor = register(new ColorSetting("UnsafeColor","Unsafe light color",1.0f,0.0f,0.0f,0.8f));
    private final IntSetting range = register(new IntSetting("Range","Display range in blocks",16,4,64));
    private final BooleanSetting showNumbers = register(new BooleanSetting("ShowNumbers","Display light level numbers",true));
    public LightLevelOverlay() { super("LightLevelOverlay","Shows light levels on blocks",Category.TWEAKEROO); }
}
