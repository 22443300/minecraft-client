package dev.phantom.client.modules.combat;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
import dev.phantom.client.core.event.EventHandler;
import dev.phantom.client.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class AutoSword extends Module {
    private final BooleanSetting switchBack = register(new BooleanSetting("Switch Back", "Switch back to previous slot after attacking", true));
    private final BooleanSetting checkDurability = register(new BooleanSetting("Check Durability", "Avoid selecting weapons with critically low durability", true));
    private final BooleanSetting preferAxe = register(new BooleanSetting("Prefer Axe", "Prefer axes over swords when damage is equal", false));

    public AutoSword() {
        super("AutoSword", "Automatically switches to the best sword/axe before attacking", Category.COMBAT);
    }
}
