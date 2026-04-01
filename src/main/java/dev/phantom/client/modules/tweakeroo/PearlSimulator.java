package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class PearlSimulator extends Module {
    private final ColorSetting color = register(new ColorSetting("Color","Trajectory color",1.0f,0.5f,0.0f,0.8f));
    private final IntSetting bounces = register(new IntSetting("Bounces","Max trajectory bounces",10,1,50));
    private final DoubleSetting thickness = register(new DoubleSetting("Thickness","Line thickness",1.5,0.5,3.0,0.1));
    private final BooleanSetting landingMarker = register(new BooleanSetting("LandingMarker","Show landing position",true));
    public PearlSimulator() { super("PearlSimulator","Visualises ender pearl trajectory",Category.TWEAKEROO); }
}
