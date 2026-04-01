package dev.phantom.client.modules.qol;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class ClearChat extends Module {
    private final IntSetting     amount    = register(new IntSetting    ("Amount",   "Number of lines to clear",          100, 10, 1000));
    private final BooleanSetting onEnable  = register(new BooleanSetting("OnEnable", "Clear chat automatically on enable", true));
    private final BooleanSetting logBefore = register(new BooleanSetting("LogBefore","Log chat to file before clearing",   false));

    public ClearChat() {
        super("ClearChat", "Clears the chat", Category.QOL);
    }
}
