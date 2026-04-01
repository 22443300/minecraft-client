package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class WalkMode extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode","Walking mode","Normal",new String[]{"Normal","Auto","Pathfind","Follow"}));
    private final DoubleSetting speed = register(new DoubleSetting("Speed","Walk speed multiplier",1.0,0.1,3.0,0.1));
    private final BooleanSetting stopOnObstacle = register(new BooleanSetting("StopOnObstacle","Stop when blocked",true));
    public WalkMode() { super("WalkMode","Advanced auto-walk modes",Category.TWEAKEROO); }
}
