package dev.phantom.client.modules.qol;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class BetterChat extends Module {
    private final BooleanSetting timestamps     = register(new BooleanSetting("Timestamps",     "Show timestamps in chat",           true));
    private final BooleanSetting antiSpam       = register(new BooleanSetting("AntiSpam",       "Suppress duplicate messages",       true));
    private final IntSetting     historySize    = register(new IntSetting    ("HistorySize",     "Max chat history lines",            200, 50, 1000));
    private final BooleanSetting chatPrefix     = register(new BooleanSetting("ChatPrefix",     "Prepend client tag to messages",    false));
    private final BooleanSetting copyOnClick    = register(new BooleanSetting("CopyOnClick",    "Copy message text on click",        true));
    private final BooleanSetting persistHistory = register(new BooleanSetting("PersistHistory", "Save chat history across sessions", false));
    private final BooleanSetting urlHighlight   = register(new BooleanSetting("UrlHighlight",   "Highlight URLs in chat",            true));

    public BetterChat() {
        super("BetterChat", "Enhances the chat interface", Category.QOL);
    }
}
