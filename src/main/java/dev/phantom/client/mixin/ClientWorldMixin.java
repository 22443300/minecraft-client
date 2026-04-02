package dev.phantom.client.mixin;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.modules.visual.TimeChanger;
import dev.phantom.client.modules.visual.WeatherChanger;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin targeting {@link ClientWorld}.
 *
 * <p>Provides hook points used by the TimeChanger and WeatherChanger visual
 * modules to override what the client believes the current world time and rain
 * gradient are, without touching server-side state.
 */
@Mixin(ClientWorld.class)
public class ClientWorldMixin {

    /**
     * Overrides the time-of-day value returned to all client-side consumers
     * (sky colour, sun/moon angle, ambient light, etc.) when the TimeChanger
     * module is active.
     *
     * <p>The returned value is a raw tick count (0 = dawn, 6000 = noon,
     * 12000 = dusk, 18000 = midnight).
     *
     * @param cir return-value callback; its value is replaced when the module
     *            is enabled and has a valid time setting
     */
    @Inject(method = "getTimeOfDay()J", at = @At("RETURN"), cancellable = true, require = 0)
    private void onGetTimeOfDay(CallbackInfoReturnable<Long> cir) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        Module m = client.modules.get("timechanger").orElse(null);
        if (m == null || !m.isEnabled()) return;

        if (TimeChanger.INSTANCE == null) return;
        long overrideTime = TimeChanger.INSTANCE.getOverrideTime();
        cir.setReturnValue(overrideTime);
        cir.cancel();
    }

    /**
     * Overrides the rain-gradient value when the WeatherChanger module is
     * active and has weather set to clear.
     *
     * <p>The rain gradient is a float in [0, 1]; 0 means no rain/thunder
     * visuals, 1 means full storm. Returning 0 from this hook produces a
     * permanently clear sky on the client regardless of the server weather.
     *
     * @param delta partial-tick interpolation factor supplied by the caller
     * @param cir   return-value callback; replaced when WeatherChanger is
     *              active and configured to show clear weather
     */
    @Inject(method = "getRainGradient(F)F", at = @At("RETURN"), cancellable = true, require = 0)
    private void onGetRainGradient(float delta, CallbackInfoReturnable<Float> cir) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        Module m = client.modules.get("weatherchanger").orElse(null);
        if (m == null || !m.isEnabled()) return;

        if (WeatherChanger.INSTANCE == null) return;
        String mode = WeatherChanger.INSTANCE.getWeatherMode();
        float override = "Clear".equals(mode) ? 0f : "Rain".equals(mode) ? 1f : cir.getReturnValue();
        cir.setReturnValue(override);
        cir.cancel();
    }
}
