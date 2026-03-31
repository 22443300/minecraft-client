package dev.phantom.client.mixin;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.module.Module;
import net.minecraft.client.render.Frustum;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Frustum.class)
public class FrustumMixin {

    /**
     * Forces all bounding boxes to report as visible when XRay is active.
     *
     * The chunk renderer calls isVisible() to decide whether a render section
     * (16x16x16 sub-chunk) should be submitted to the GPU. By always returning
     * true we prevent any section from being culled, which means XRay can show
     * ore blocks inside sections that would normally be behind the camera or
     * outside the view cone.
     *
     * This comes with a performance cost: every section in render distance is
     * drawn. XRay should therefore be used with a reduced render distance or
     * the performance impact may be significant on large servers.
     *
     * The ESP entity path is handled in EntityRenderDispatcherMixin via the
     * shouldRender() override; this frustum hook is purely for block-level
     * visibility.
     */
    @Inject(method = "isVisible", at = @At("RETURN"), cancellable = true)
    private void onIsVisible(Box box, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return; // Already visible – no need to override

        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        Module xray = client.modules.get("xray").orElse(null);
        if (xray != null && xray.isEnabled()) {
            cir.setReturnValue(true);
        }
    }
}
