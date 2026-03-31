package dev.phantom.client.mixin;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.event.EventBus;
import dev.phantom.client.core.event.events.RenderWorldEvent;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    /**
     * Posts RenderWorldEvent at the very end of the world render pass.
     *
     * The positionMatrix (last Matrix4f parameter) is used to initialise a
     * fresh MatrixStack so that any rendering code listening to RenderWorldEvent
     * already has the correct world-space transform applied.
     *
     * Note: Fabric API's WorldRenderEvents.AFTER_ENTITIES is registered in
     * PhantomClient.java and also posts RenderWorldEvent. This injection
     * provides a second, lower-level hook that fires after ALL geometry
     * (including translucent layers) has been submitted.
     */
    @Inject(
        method = "render",
        at = @At("TAIL")
    )
    private void onRenderTail(RenderTickCounter tickCounter,
                              boolean renderBlockOutline,
                              Camera camera,
                              GameRenderer gameRenderer,
                              LightmapTextureManager lightmapTextureManager,
                              Matrix4f frustumMatrix,
                              Matrix4f positionMatrix,
                              CallbackInfo ci) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        MatrixStack matrices = new MatrixStack();
        matrices.multiplyPositionMatrix(positionMatrix);

        EventBus.INSTANCE.post(new RenderWorldEvent(matrices, tickCounter.getTickDelta(false), camera));
    }
}
