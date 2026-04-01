package dev.phantom.client.modules.combat;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
import dev.phantom.client.core.event.EventHandler;
import dev.phantom.client.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class AutoGap extends Module {
    private final DoubleSetting health = register(new DoubleSetting("Health", "Health level at which to start eating golden apple", 15.0, 1.0, 20.0, 0.5));
    private final BooleanSetting preferNotchApple = register(new BooleanSetting("Prefer Notch Apple", "Prefer enchanted golden apple over regular", true));
    private final BooleanSetting eatWhileBlocking = register(new BooleanSetting("Eat While Blocking", "Allow eating while blocking with a shield", true));

    public AutoGap() {
        super("AutoGap", "Automatically eats golden apples when health is low", Category.COMBAT);
    }
}
