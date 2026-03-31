package dev.phantom.client.mixin;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.module.Module;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {

    /**
     * Entry point for Chams rendering.
     *
     * When the Chams module is enabled we want entities to be visible through
     * walls. The approach taken here is to inject at the HEAD of the render
     * method so we can push render state before vanilla draws the entity, and
     * then restore it at TAIL via the second injection below.
     *
     * The actual wall-visibility effect is achieved by rendering a second pass
     * with GL depth-test disabled; that pass is issued in the TAIL injection so
     * it draws on top of any solid geometry.
     *
     * For a lightweight first implementation this injection only gates the
     * logic – the per-entity rendering layers are added by the Chams module
     * through its own EventHandler on RenderWorldEvent.
     */
    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderHead(T entity, float yaw, float tickDelta,
                              MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                              int light, CallbackInfo ci) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        Module chams = client.modules.get("chams").orElse(null);
        if (chams == null || !chams.isEnabled()) return;

        // Future: push a custom RenderLayer or set GL state here so the entity
        // model renders with a special shader (e.g. solid colour + no depth test).
        // The Chams module's own RenderWorldEvent handler issues the secondary
        // render pass after all entity geometry has been submitted.
    }

    /**
     * Tail hook that restores any render state pushed in the HEAD injection.
     */
    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderTail(T entity, float yaw, float tickDelta,
                              MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                              int light, CallbackInfo ci) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        Module chams = client.modules.get("chams").orElse(null);
        if (chams == null || !chams.isEnabled()) return;

        // Future: pop GL state / render layer here if any was pushed in HEAD.
    }
}
