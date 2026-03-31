package dev.phantom.client.modules.visual;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.IntSetting;

/**
 * TimeChanger – overrides the client-side world time of day.
 *
 * <p>The mixin {@code ClientWorldMixin} reads {@link #getOverrideTime()} to
 * replace the value returned by {@code ClientWorld.getTimeOfDay()} while this
 * module is enabled.
 *
 * <p>Time values follow vanilla convention:
 * <pre>
 *   0     = sunrise / dawn
 *   6000  = noon (sun overhead)
 *   12000 = sunset / dusk
 *   18000 = midnight
 * </pre>
 */
public class TimeChanger extends Module {

    /** Ticks: 0 = dawn, 6000 = noon, 12000 = dusk, 18000 = midnight. */
    private final IntSetting time = register(
            new IntSetting("Time", "World time in ticks", 6000, 0, 24000));

    public TimeChanger() {
        super("TimeChanger", "Changes the visual time of day", Category.VISUAL);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    // -------------------------------------------------------------------------
    // Static API used by ClientWorldMixin
    // -------------------------------------------------------------------------

    /**
     * Returns the configured world-time override in ticks.
     *
     * <p>Called by {@code ClientWorldMixin#onGetTimeOfDay} every frame; must
     * be cheap (no allocation, no synchronisation needed for a plain field
     * read).
     */
    public static long getOverrideTime() {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return 6000L;

        Module m = client.modules.get("timechanger").orElse(null);
        if (m instanceof TimeChanger tc) {
            return tc.time.getValue().longValue();
        }
        return 6000L;
    }
}
