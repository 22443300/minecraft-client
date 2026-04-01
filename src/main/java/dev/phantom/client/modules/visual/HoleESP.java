package dev.phantom.client.modules.visual;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class HoleESP extends Module {
    private final BooleanSetting obsidian = register(new BooleanSetting("Obsidian", "Highlight obsidian holes", true));
    private final BooleanSetting bedrock = register(new BooleanSetting("Bedrock", "Highlight bedrock holes", true));
    private final BooleanSetting mixed = register(new BooleanSetting("Mixed", "Highlight mixed obsidian/bedrock holes", true));
    private final IntSetting range = register(new IntSetting("Range", "Hole ESP search range", 8, 4, 32));
    private final ColorSetting safeColor = register(new ColorSetting("Safe Color", "Color for safe holes", 0.0f, 1.0f, 0.0f, 0.6f));
    private final ColorSetting unsafeColor = register(new ColorSetting("Unsafe Color", "Color for unsafe holes", 1.0f, 0.5f, 0.0f, 0.6f));

    public HoleESP() {
        super("HoleESP", "Highlights safe holes for crystal PvP", Category.VISUAL);
    }
}
