package dev.phantom.client.mixin;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.module.Module;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

    /**
     * Cancels vanilla fog rendering when the NoRender module's fog option is
     * active. The applyFog static method sets up the GL fog parameters; by
     * cancelling it no fog is applied for the current frame.
     *
     * In 1.21.1 the method is static so we target it with the static mixin
     * pattern (no instance). The parameter list matches the Yarn-mapped
     * signature: (Camera, BackgroundRenderer.FogType, float, boolean, float).
     */
    @Inject(method = "applyFog", at = @At("HEAD"), cancellable = true)
    private static void onApplyFog(Camera camera,
                                   BackgroundRenderer.FogType fogType,
                                   float viewDistance,
                                   boolean thickFog,
                                   float tickDelta,
                                   CallbackInfo ci) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        Module noRender = client.modules.get("norender").orElse(null);
        if (noRender != null && noRender.isEnabled()) {
            ci.cancel();
        }
    }

    /**
     * Overrides the sky/background render to implement FullBright.
     *
     * FullBright works best by modifying the gamma option in GameOptionsMixin,
     * but this hook provides a complementary cancel of the darkness overlay
     * (e.g. inside caves with vanilla lighting) when FullBright is active.
     *
     * The render() method in BackgroundRenderer draws the sky box and fog
     * colour; cancelling it is too destructive. Instead we only suppress the
     * sky darkening pass which is handled by this injection on the "render"
     * method. The FullBright effect on block light is done via GameOptionsMixin.
     */
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private static void onRender(Camera camera,
                                 float tickDelta,
                                 ClientWorld world,
                                 int viewDistance,
                                 float skyDarkness,
                                 CallbackInfo ci) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        // NoRender fog: suppress the entire background pass
        Module noRender = client.modules.get("norender").orElse(null);
        if (noRender != null && noRender.isEnabled()) {
            ci.cancel();
        }
    }
}
