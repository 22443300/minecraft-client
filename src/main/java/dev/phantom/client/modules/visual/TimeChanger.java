package dev.phantom.client.modules.visual;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class TimeChanger extends Module {
    private final IntSetting time = register(new IntSetting("Time", "World time in ticks", 6000, 0, 24000));
    private final ModeSetting mode = register(new ModeSetting("Mode", "Time preset or custom value", "Custom", new String[]{"Custom", "Day", "Night", "Sunset", "Midnight"}));

    public static TimeChanger INSTANCE;

    public TimeChanger() {
        super("TimeChanger", "Changes the visual time of day", Category.VISUAL);
        INSTANCE = this;
    }

    /** Called by ClientWorldMixin to get the override time. Returns -1 if disabled. */
    public long getOverrideTime() {
        if (!isEnabled()) return -1L;
        String m = mode.getValue();
        return switch (m) {
            case "Day"     -> 6000L;
            case "Night"   -> 18000L;
            case "Sunset"  -> 12000L;
            case "Midnight"-> 18000L;
            default         -> (long) time.getValue();
        };
    }
}
