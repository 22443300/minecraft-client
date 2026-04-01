package dev.phantom.client.modules.utility;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class AutoReply extends Module {
    private final StringSetting message = register(new StringSetting("Message", "Auto reply message", "I am using Phantom Client, brb"));
    private final IntSetting delay = register(new IntSetting("Delay", "Ticks between auto replies", 20, 5, 200));
    private final BooleanSetting onlyFromFriends = register(new BooleanSetting("Only From Friends", "Only reply to messages from friends", false));
    private final BooleanSetting logWhispers = register(new BooleanSetting("Log Whispers", "Log received whispers to chat", true));

    public AutoReply() {
        super("AutoReply", "Automatically replies to messages", Category.UTILITY);
    }
}
