package dev.phantom.client.mixin;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.module.Module;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin targeting {@link ItemUsageContext}.
 *
 * <p>Provides an injection point for the FlexibleBlockPlacement module.
 * That module allows blocks to be placed on any face regardless of the exact
 * hit direction, which normally restricts placement to the specific side the
 * crosshair is pointing at.
 *
 * <p>The {@code getSide()} accessor is the lightest-weight interception point:
 * we simply replace the returned {@link Direction} with whichever face the
 * module has chosen (typically the nearest perpendicular face to the player's
 * look vector, computed server-side-compatible).
 */
@Mixin(ItemUsageContext.class)
public class ItemUsageContextMixin {

    /**
     * Overrides the side (face direction) returned by {@link ItemUsageContext#getSide()}.
     *
     * <p>When FlexibleBlockPlacement is enabled the module computes the best
     * placement face independently and stores it in its own state. We retrieve
     * that override here instead of letting the raw hit-result face through.
     *
     * @param cir the return-value callback; its return value is replaced when
     *            the module is active and has a valid override face
     */
    @Inject(method = "getSide", at = @At("RETURN"), cancellable = true)
    private void onGetSide(CallbackInfoReturnable<Direction> cir) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        Module flexPlace = client.modules.get("flexibleblockplacement").orElse(null);
        if (flexPlace == null || !flexPlace.isEnabled()) return;

        // FlexibleBlockPlacement exposes its preferred placement face via a
        // static field so this mixin does not need to cast to the concrete type
        // and can remain decoupled from the module's implementation details.
        Direction override =
                dev.phantom.client.modules.tweakeroo.FlexibleBlockPlacement.getPlacementFace();
        if (override != null) {
            cir.setReturnValue(override);
            cir.cancel();
        }
    }
}
