package dev.phantom.client.modules.qol;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class WindowTitle extends Module {
    private final StringSetting title = register(new StringSetting("Title","Window title format","Phantom Client — %server%"));
    private final BooleanSetting showFps = register(new BooleanSetting("ShowFPS","Append FPS to title",false));
    private final BooleanSetting showPing = register(new BooleanSetting("ShowPing","Append ping to title",false));
    public WindowTitle() { super("WindowTitle","Customizes the Minecraft window title",Category.QOL); }
}
