package dev.phantom.client.modules.utility;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class AutoFish extends Module {
    private final IntSetting reelDelay = register(new IntSetting("Reel Delay", "Ticks to wait before reeling in", 3, 0, 20));
    private final BooleanSetting antiAfk = register(new BooleanSetting("Anti AFK", "Move slightly to prevent AFK kick", true));
    private final BooleanSetting autoReel = register(new BooleanSetting("Auto Reel", "Automatically reel in when a fish bites", true));
    private final BooleanSetting sound = register(new BooleanSetting("Sound", "Play a sound when a fish bites", true));
    private final BooleanSetting alertInChat = register(new BooleanSetting("Alert In Chat", "Send a chat message when a fish is caught", false));

    public AutoFish() {
        super("AutoFish", "Automatically fishes", Category.UTILITY);
    }
}
