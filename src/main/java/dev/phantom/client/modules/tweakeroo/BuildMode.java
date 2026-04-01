package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class BuildMode extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode","Build mode","Grid",new String[]{"Grid","Layer","Fill","Mirror"}));
    private final IntSetting gridSize = register(new IntSetting("GridSize","Grid snap size",1,1,16));
    private final BooleanSetting snapToGrid = register(new BooleanSetting("SnapToGrid","Snap placements to grid",true));
    private final ModeSetting mirrorAxis = register(new ModeSetting("MirrorAxis","Mirror axis","X",new String[]{"X","Z","Both"}));
    public BuildMode() { super("BuildMode","Enhanced building modes with grid/mirror support",Category.TWEAKEROO); }
}
