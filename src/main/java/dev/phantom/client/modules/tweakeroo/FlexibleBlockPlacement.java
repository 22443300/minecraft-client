package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
import net.minecraft.util.math.Direction;
public class FlexibleBlockPlacement extends Module {
    private static Direction currentFace = null;
    public static FlexibleBlockPlacement INSTANCE;
    private final ModeSetting mode = register(new ModeSetting("Mode","Placement mode","Adjacent",new String[]{"Adjacent","Any","Force"}));
    private final BooleanSetting rotationFix = register(new BooleanSetting("RotationFix","Fix rotation for placement",true));
    private final BooleanSetting strictAdj = register(new BooleanSetting("StrictAdj","Strict adjacency check",false));
    public FlexibleBlockPlacement() { super("FlexibleBlockPlacement","Place blocks on any face",Category.TWEAKEROO); INSTANCE=this; }
    public static Direction getPlacementFace() { return currentFace; }
    public static void setPlacementFace(Direction face) { currentFace = face; }
    @Override public void onEnable() { currentFace=null; }
    @Override public void onDisable() { currentFace=null; }
}
