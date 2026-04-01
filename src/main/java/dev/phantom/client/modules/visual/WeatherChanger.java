package dev.phantom.client.modules.visual;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class WeatherChanger extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "Visual weather mode", "Clear", new String[]{"Clear", "Rain", "Thunder"}));

    public static WeatherChanger INSTANCE;

    public WeatherChanger() {
        super("WeatherChanger", "Changes the visual weather", Category.VISUAL);
        INSTANCE = this;
    }

    /** Called by ClientWorldMixin. Returns null if disabled, else "Clear"/"Rain"/"Thunder". */
    public String getWeatherMode() {
        return isEnabled() ? mode.getValue() : null;
    }
}
