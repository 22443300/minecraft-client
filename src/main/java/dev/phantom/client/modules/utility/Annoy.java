package dev.phantom.client.modules.utility;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class Annoy extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "Annoy mode type", "Spam", new String[]{"Spam", "Swear", "Troll"}));
    private final IntSetting delay = register(new IntSetting("Delay", "Ticks between annoy actions", 40, 10, 200));
    private final StringSetting prefix = register(new StringSetting("Prefix", "Message prefix", "[Phantom]"));
    private final BooleanSetting randomize = register(new BooleanSetting("Randomize", "Randomize message content", true));

    public Annoy() {
        super("Annoy", "Annoys nearby players", Category.UTILITY);
    }
}
