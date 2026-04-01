package dev.phantom.client.modules.utility;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class Disconnect extends Module {
    private final DoubleSetting health = register(new DoubleSetting("Health", "Disconnect when health falls below this value", 6.0, 1.0, 20.0, 0.5));
    private final BooleanSetting onTotemPop = register(new BooleanSetting("On Totem Pop", "Disconnect when a totem of undying is consumed", false));
    private final BooleanSetting onBedObstruct = register(new BooleanSetting("On Bed Obstruct", "Disconnect when the bed is obstructed", false));
    private final BooleanSetting key = register(new BooleanSetting("Key", "Enable a keybind to instantly disconnect", false));
    private final StringSetting message = register(new StringSetting("Message", "Chat message before disconnect", "Bye!"));

    public Disconnect() {
        super("Disconnect", "Disconnects from the server under specified conditions", Category.UTILITY);
    }
}
