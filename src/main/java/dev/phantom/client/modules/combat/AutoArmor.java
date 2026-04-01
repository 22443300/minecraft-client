package dev.phantom.client.modules.combat;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
import dev.phantom.client.core.event.EventHandler;
import dev.phantom.client.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class AutoArmor extends Module {
    private final IntSetting delay = register(new IntSetting("Delay", "Delay between armor equip checks in ticks", 5, 0, 20));
    private final BooleanSetting preferProtection = register(new BooleanSetting("Prefer Protection", "Prefer Protection enchantment when selecting armor", true));
    private final BooleanSetting hotbar = register(new BooleanSetting("Hotbar", "Allow equipping armor from hotbar slots", false));
    private final BooleanSetting unbreaking = register(new BooleanSetting("Unbreaking", "Prefer Unbreaking enchantment when selecting armor", true));

    public AutoArmor() {
        super("AutoArmor", "Automatically equips the best armor", Category.COMBAT);
    }
}
