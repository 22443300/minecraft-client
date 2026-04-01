package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class PlacementGhost extends Module {
    private final ColorSetting color = register(new ColorSetting("Color","Ghost block color",0.5f,0.5f,1.0f,0.3f));
    private final IntSetting opacity = register(new IntSetting("Opacity","Ghost opacity 0-255",60,0,255));
    private final BooleanSetting showInvalid = register(new BooleanSetting("ShowInvalid","Show invalid placements",true));
    private final ColorSetting invalidColor = register(new ColorSetting("InvalidColor","Invalid placement color",1.0f,0.0f,0.0f,0.3f));
    public PlacementGhost() { super("PlacementGhost","Shows ghost preview of block placement",Category.TWEAKEROO); }
}
