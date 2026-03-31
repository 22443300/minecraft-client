package dev.phantom.client.mixin;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.module.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin targeting the creative-mode inventory screen.
 *
 * <p>Mirrors the hook points from {@link HandledScreenMixin} for the creative
 * inventory, which does not extend {@code HandledScreen} in all versions and
 * therefore needs its own {@code @Mixin} target.
 */
@Mixin(CreativeInventoryScreen.class)
public class CreativeInventoryScreenMixin {

    /**
     * Intercepts mouse clicks on the creative inventory.
     *
     * <p>Behaviour mirrors {@code HandledScreenMixin#onMouseClicked}: checks
     * NoInteract first, then InventoryTweaks middle-click sort.
     */
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(double mouseX, double mouseY, int button,
                                CallbackInfoReturnable<Boolean> cir) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        // NoInteract: block all slot interactions
        Module noInteract = client.modules.get("nointeract").orElse(null);
        if (noInteract != null && noInteract.isEnabled()) {
            cir.setReturnValue(false);
            cir.cancel();
            return;
        }

        // InventoryTweaks middle-click sort
        if (button == 2) {
            Module invTweaks = client.modules.get("inventorytweaks").orElse(null);
            if (invTweaks != null && invTweaks.isEnabled()) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }

    /**
     * Allows modules to draw overlays on top of the creative inventory.
     */
    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta,
                          CallbackInfo ci) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        // InventoryPreview and similar modules hook here if needed
        Module preview = client.modules.get("inventorypreview").orElse(null);
        if (preview != null && preview.isEnabled()) {
            // Module handles rendering through its own event subscription
        }
    }
}
