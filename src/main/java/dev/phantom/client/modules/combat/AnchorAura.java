package dev.phantom.client.modules.combat;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;

public class AnchorAura extends Module {

    public AnchorAura() {
        super("AnchorAura", "Automatically activates respawn anchors near enemies", Category.COMBAT);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
