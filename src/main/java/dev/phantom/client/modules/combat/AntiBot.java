package dev.phantom.client.modules.combat;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
import dev.phantom.client.core.event.EventHandler;
import dev.phantom.client.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class AntiBot extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "Bot detection method", "Heuristic", new String[]{"Heuristic", "Strict", "Whitelist"}));
    private final BooleanSetting checkPing = register(new BooleanSetting("Check Ping", "Flag players with suspiciously low or no ping", true));
    private final BooleanSetting checkSkin = register(new BooleanSetting("Check Skin", "Flag players with default or missing skin", false));
    private final BooleanSetting checkUUID = register(new BooleanSetting("Check UUID", "Flag players with invalid or cracked UUIDs", true));
    private final BooleanSetting hideFromESP = register(new BooleanSetting("Hide From ESP", "Hide detected bots from ESP overlays", true));

    public AntiBot() {
        super("AntiBot", "Filters and ignores bot players in combat modules", Category.COMBAT);
    }
}
