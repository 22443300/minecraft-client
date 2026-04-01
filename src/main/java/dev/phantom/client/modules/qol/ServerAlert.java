package dev.phantom.client.modules.qol;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class ServerAlert extends Module {
    private final BooleanSetting playerJoin = register(new BooleanSetting("PlayerJoin","Alert on player join",true));
    private final BooleanSetting playerLeave = register(new BooleanSetting("PlayerLeave","Alert on player leave",false));
    private final BooleanSetting friendJoin = register(new BooleanSetting("FriendJoin","Alert when friend joins",true));
    private final BooleanSetting lowTPS = register(new BooleanSetting("LowTPS","Alert on low TPS",true));
    private final DoubleSetting tpsThreshold = register(new DoubleSetting("TPSThreshold","TPS warning threshold",15.0,1.0,20.0,0.5));
    private final BooleanSetting sound = register(new BooleanSetting("Sound","Play alert sound",true));
    public ServerAlert() { super("ServerAlert","Alerts you to important server events",Category.QOL); }
}
