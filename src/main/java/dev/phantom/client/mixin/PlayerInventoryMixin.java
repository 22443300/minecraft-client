package dev.phantom.client.mixin;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.module.Module;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin targeting {@link PlayerInventory}.
 *
 * <p>Intercepts {@code scrollInHotbar} so that the HotbarScroll module can
 * override or suppress the default hotbar-slot cycling behaviour (for example,
 * to implement locked-slot scrolling or scroll-to-tool logic).
 */
@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

    /**
     * Injected at HEAD of {@code scrollInHotbar}, cancellable.
     *
     * <p>When the HotbarScroll module is enabled it takes full ownership of
     * the scroll action. The vanilla implementation is cancelled so that the
     * module can apply its own logic (e.g. skip locked slots, cycle in a
     * custom direction, or silently drop the scroll event while another item
     * is being held).
     *
     * @param scrollAmount the raw scroll delta received from GLFW; positive
     *                     values scroll forward (left on most mice), negative
     *                     values scroll back
     * @param ci           mixin callback; cancelled when HotbarScroll handles
     *                     the event itself
     */
    @Inject(method = "scrollInHotbar", at = @At("HEAD"), cancellable = true)
    private void onScrollInHotbar(double scrollAmount, CallbackInfo ci) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        Module hotbarScroll = client.modules.get("hotbarscroll").orElse(null);
        if (hotbarScroll == null || !hotbarScroll.isEnabled()) return;

        // HotbarScroll handles slot selection in its own TickEvent / scroll
        // handler; cancel vanilla processing here so the two don't conflict.
        ci.cancel();
    }
}
