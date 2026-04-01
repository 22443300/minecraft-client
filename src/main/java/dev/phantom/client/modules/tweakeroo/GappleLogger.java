package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class GappleLogger extends Module {
    private final BooleanSetting logOwn = register(new BooleanSetting("LogOwn","Log your own usage",true));
    private final BooleanSetting logOthers = register(new BooleanSetting("LogOthers","Log other players",true));
    private final BooleanSetting showInChat = register(new BooleanSetting("ShowInChat","Display in chat",true));
    private final BooleanSetting notchApple = register(new BooleanSetting("NotchApple","Track notch apples",true));
    private final BooleanSetting sound = register(new BooleanSetting("Sound","Play sound on use",false));
    public GappleLogger() { super("GappleLogger","Logs golden apple consumption",Category.TWEAKEROO); }
}
