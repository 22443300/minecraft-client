package dev.phantom.client.modules.combat;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;

public class KillAura extends Module {

    public KillAura() {
        super("KillAura", "Automatically attacks nearby entities", Category.COMBAT);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
