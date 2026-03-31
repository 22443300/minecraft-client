package dev.phantom.client.mixin;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.module.Module;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    /**
     * Modifies the field-of-view used by the renderer.
     *
     * The Zoom module reduces the FOV to simulate a spyglass-style zoom.
     * The zoom factor is fixed at 0.25 (four-times zoom) here; a future
     * DoubleSetting on the Zoom module can be read instead once it exposes
     * one. The RETURN injection point sees the final FOV value that vanilla
     * computed and lets us replace it before it is used.
     */
    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void onGetFov(Camera camera, float tickDelta, boolean changingFov,
                          CallbackInfoReturnable<Float> cir) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        Module zoom = client.modules.get("zoom").orElse(null);
        if (zoom != null && zoom.isEnabled()) {
            // Reduce FOV to 25 % of whatever vanilla computed (four-times zoom)
            float original = cir.getReturnValue();
            cir.setReturnValue(original * 0.25f);
        }
    }
}
