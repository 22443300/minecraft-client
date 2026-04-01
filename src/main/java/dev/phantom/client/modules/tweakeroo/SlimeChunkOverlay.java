package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class SlimeChunkOverlay extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode","Overlay style","Outline",new String[]{"Outline","Fill","Both"}));
    private final ColorSetting color = register(new ColorSetting("Color","Overlay color",0.0f,0.8f,0.0f,0.4f));
    private final BooleanSetting showCoords = register(new BooleanSetting("ShowCoords","Show chunk coordinates",false));
    public SlimeChunkOverlay() { super("SlimeChunkOverlay","Highlights slime spawn chunks",Category.TWEAKEROO); }
}
