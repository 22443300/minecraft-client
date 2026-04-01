package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class MobSpawnOverlay extends Module {
    private final BooleanSetting showSafe = register(new BooleanSetting("ShowSafe","Show safe spawn areas",false));
    private final BooleanSetting showUnsafe = register(new BooleanSetting("ShowUnsafe","Show unsafe spawn areas",true));
    private final BooleanSetting showWarning = register(new BooleanSetting("ShowWarning","Show warning areas",true));
    private final IntSetting range = register(new IntSetting("Range","Check radius",16,4,64));
    private final ColorSetting safeColor = register(new ColorSetting("SafeColor","Safe area color",0.0f,1.0f,0.0f,0.5f));
    private final ColorSetting unsafeColor = register(new ColorSetting("UnsafeColor","Unsafe area color",1.0f,0.0f,0.0f,0.5f));
    public MobSpawnOverlay() { super("MobSpawnOverlay","Shows where mobs can spawn",Category.TWEAKEROO); }
}
