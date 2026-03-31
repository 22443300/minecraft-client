package dev.phantom.client.mixin;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.event.EventBus;
import dev.phantom.client.core.event.events.RenderHudEvent;
import dev.phantom.client.core.module.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    /**
     * Posts RenderHudEvent after the entire HUD has been drawn so that
     * Phantom's own HUD elements render on top.
     */
    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderTail(DrawContext drawContext, RenderTickCounter tickCounter, CallbackInfo ci) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;
        EventBus.INSTANCE.post(new RenderHudEvent(drawContext, tickCounter.getTickDelta(false)));
    }

    /**
     * Allows modules to suppress the vanilla crosshair.
     * Currently no module uses this, but the hook is here for future use
     * (e.g. a custom crosshair renderer that replaces the vanilla one).
     */
    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void onRenderCrosshair(DrawContext drawContext, RenderTickCounter tickCounter, CallbackInfo ci) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;
        // Example: a "CustomCrosshair" module could cancel vanilla crosshair here.
        // Module customCrosshair = client.modules.get("customcrosshair").orElse(null);
        // if (customCrosshair != null && customCrosshair.isEnabled()) ci.cancel();
    }
}
