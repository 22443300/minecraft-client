package dev.phantom.client.modules.utility;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class ChatFilter extends Module {
    private final BooleanSetting hideJoin = register(new BooleanSetting("Hide Join", "Hide join messages", false));
    private final BooleanSetting hideLeave = register(new BooleanSetting("Hide Leave", "Hide leave messages", false));
    private final BooleanSetting hideDeaths = register(new BooleanSetting("Hide Deaths", "Hide death messages", false));
    private final BooleanSetting antiSpam = register(new BooleanSetting("Anti Spam", "Filter repeated messages", true));
    private final StringSetting customFilter = register(new StringSetting("Custom Filter", "Regex patterns (semicolon separated)", ""));
    private final BooleanSetting blockCommands = register(new BooleanSetting("Block Commands", "Hide command output messages", false));

    public ChatFilter() {
        super("ChatFilter", "Filters chat messages", Category.UTILITY);
    }
}
