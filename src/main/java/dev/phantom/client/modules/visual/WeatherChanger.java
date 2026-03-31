package dev.phantom.client.modules.visual;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.EnumSetting;

/**
 * WeatherChanger – overrides the client-side rain/thunder gradient.
 *
 * <p>The mixin {@code ClientWorldMixin} reads {@link #getRainGradient()} to
 * replace the value returned by {@code ClientWorld.getRainGradient(float)}
 * while this module is enabled, producing a permanently clear or stormy sky
 * without touching server state.
 */
public class WeatherChanger extends Module {

    public enum WeatherMode { CLEAR, RAIN, THUNDER }

    private final EnumSetting<WeatherMode> mode = register(
            new EnumSetting<>("Mode", "Visual weather mode",
                    WeatherMode.CLEAR, WeatherMode.class));

    public WeatherChanger() {
        super("WeatherChanger", "Changes the visual weather", Category.VISUAL);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    // -------------------------------------------------------------------------
    // Static API used by ClientWorldMixin
    // -------------------------------------------------------------------------

    /**
     * Returns the rain-gradient override value (0.0 = clear, 1.0 = full storm)
     * based on the current module configuration.
     */
    public static float getRainGradient() {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return 0f;

        Module m = client.modules.get("weatherchanger").orElse(null);
        if (m instanceof WeatherChanger wc) {
            return switch (wc.mode.getValue()) {
                case CLEAR   -> 0f;
                case RAIN    -> 1f;
                case THUNDER -> 1f;
            };
        }
        return 0f;
    }
}
