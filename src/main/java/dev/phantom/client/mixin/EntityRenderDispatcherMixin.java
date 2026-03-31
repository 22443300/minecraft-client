package dev.phantom.client.mixin;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.module.Module;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    /**
     * Bypasses frustum culling for entities when ESP or Chams is active.
     *
     * Vanilla returns false if the entity's bounding box is outside the view
     * frustum, which would suppress ESP outlines and Chams overlays for
     * off-screen entities. Returning true forces the entity through the render
     * pipeline so those visual effects can be seen through walls regardless of
     * the camera direction.
     *
     * Only players and living mobs are forced visible to avoid unnecessary
     * rendering load from projectiles, dropped items, etc.
     */
    @Inject(method = "shouldRender", at = @At("RETURN"), cancellable = true)
    private <E extends Entity> void onShouldRender(E entity, Frustum frustum,
                                                   double x, double y, double z,
                                                   CallbackInfoReturnable<Boolean> cir) {
        // Only force-render when vanilla would have culled the entity
        if (cir.getReturnValue()) return;

        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        Module esp   = client.modules.get("esp").orElse(null);
        Module chams = client.modules.get("chams").orElse(null);

        boolean espActive   = esp   != null && esp.isEnabled();
        boolean chamsActive = chams != null && chams.isEnabled();

        if (!espActive && !chamsActive) return;

        // Restrict to players and living entities to limit performance impact
        if (entity instanceof net.minecraft.entity.LivingEntity) {
            cir.setReturnValue(true);
        }
    }
}
