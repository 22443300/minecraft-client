package dev.phantom.client.modules.combat;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
import dev.phantom.client.core.event.EventHandler;
import dev.phantom.client.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class AutoLog extends Module {
    private final DoubleSetting healthThreshold = register(new DoubleSetting("Health Threshold", "Health level at which to auto-disconnect", 8.0, 1.0, 20.0, 0.5));
    private final StringSetting message = register(new StringSetting("Message", "Message in chat before disconnect", "Phantom: Auto-logged!"));
    private final BooleanSetting onCrystal = register(new BooleanSetting("On Crystal", "Auto-log when hit by end crystal", true));
    private final BooleanSetting onTotemPop = register(new BooleanSetting("On Totem Pop", "Auto-log when totem of undying pops", true));

    public AutoLog() {
        super("AutoLog", "Automatically disconnects when health is critically low", Category.COMBAT);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.getNetworkHandler() == null) return;
        if (mc.player.getHealth() <= healthThreshold.getValue().floatValue()) {
            mc.getNetworkHandler().getConnection().disconnect(Text.literal(message.getValue()));
        }
    }
}
