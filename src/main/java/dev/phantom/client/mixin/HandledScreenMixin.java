package dev.phantom.client.mixin;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.module.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin targeting all handled (container) screens.
 *
 * <p>Provides two hook points for client modules:
 * <ul>
 *   <li>{@code mouseClicked} – lets ChestStealer, InventoryTweaks and
 *       NoInteract intercept mouse presses on slots before vanilla processes
 *       them.</li>
 *   <li>{@code render} (tail) – lets modules (e.g. InventoryPreview) draw
 *       additional overlays on top of any inventory screen.</li>
 * </ul>
 */
@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen {

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    /**
     * Injected at HEAD of {@code mouseClicked}.
     *
     * <p>Checks the following modules in order:
     * <ol>
     *   <li><b>NoInteract</b> – if enabled, cancels all mouse interactions
     *       with the inventory entirely.</li>
     *   <li><b>InventoryTweaks</b> – middle-click (button == 2) triggers a
     *       sort request; the actual slot manipulation is handled inside the
     *       module to keep this mixin thin.</li>
     * </ol>
     */
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(double mouseX, double mouseY, int button,
                                CallbackInfoReturnable<Boolean> cir) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        // NoInteract: block all interactions with inventory screens
        Module noInteract = client.modules.get("nointeract").orElse(null);
        if (noInteract != null && noInteract.isEnabled()) {
            cir.setReturnValue(false);
            cir.cancel();
            return;
        }

        // InventoryTweaks: middle-click (button 2) triggers sort
        if (button == 2) {
            Module invTweaks = client.modules.get("inventorytweaks").orElse(null);
            if (invTweaks != null && invTweaks.isEnabled()) {
                // Delegate the sort action to the module itself
                // The module listens for this pattern via its own logic;
                // we cancel vanilla's pick-block behavior so it doesn't
                // duplicate items in creative mode.
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }

    /**
     * Injected at TAIL of {@code render}.
     *
     * <p>Allows any module that needs to overlay information on top of open
     * inventory screens (e.g. InventoryPreview showing item tooltip overlays)
     * to do so after vanilla has finished painting the screen.
     */
    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta,
                          CallbackInfo ci) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        // InventoryPreview overlay
        Module preview = client.modules.get("inventorypreview").orElse(null);
        if (preview != null && preview.isEnabled()) {
            // The module renders its overlay independently via its own
            // RenderHudEvent / event handler; nothing extra needed here.
        }
    }
}
