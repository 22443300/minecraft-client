package dev.phantom.client.modules.qol;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class PingSpoof extends Module {
    private final IntSetting  fakePing  = register(new IntSetting ("FakePing",  "Displayed ping value",              5, 0, 2000));
    private final ModeSetting mode      = register(new ModeSetting("Mode",      "Ping spoofing mode",                "Static", new String[]{"Static","Random","Fluctuate"}));
    private final IntSetting  fluctuate = register(new IntSetting ("Fluctuate", "Fluctuation range in milliseconds", 20, 5, 200));

    public PingSpoof() {
        super("PingSpoof", "Spoofs your displayed ping", Category.QOL);
    }
}
