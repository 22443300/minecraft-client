package dev.phantom.client.modules.utility;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class ChatSpammer extends Module {
    private final StringSetting message = register(new StringSetting("Message", "Message to spam", "Check out Phantom Client!"));
    private final IntSetting delay = register(new IntSetting("Delay", "Ticks between messages", 100, 20, 1200));
    private final BooleanSetting randomize = register(new BooleanSetting("Randomize", "Randomize message content slightly", false));
    private final BooleanSetting bypass = register(new BooleanSetting("Bypass", "Use bypass techniques to avoid spam filters", false));
    private final StringSetting prefix = register(new StringSetting("Prefix", "Optional prefix", ""));

    public ChatSpammer() {
        super("ChatSpammer", "Spams a message in chat at a set interval", Category.UTILITY);
    }
}
