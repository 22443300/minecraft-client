package dev.phantom.client.mixin;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    /**
     * Forces all entities to appear visible to the local player when ESP or
     * Chams is active.
     *
     * Vanilla returns true (invisible) for entities that have the invisibility
     * effect AND the observer has no special permissions to see through it.
     * Overriding this ensures ESP outlines and Chams overlays are drawn even
     * on invisible entities.
     */
    @Inject(method = "isInvisibleTo", at = @At("RETURN"), cancellable = true)
    private void onIsInvisibleTo(PlayerEntity observer, CallbackInfoReturnable<Boolean> cir) {
        // Only override when the result would be "invisible" (true)
        if (!cir.getReturnValue()) return;

        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        Module esp   = client.modules.get("esp").orElse(null);
        Module chams = client.modules.get("chams").orElse(null);

        boolean espActive   = esp   != null && esp.isEnabled();
        boolean chamsActive = chams != null && chams.isEnabled();

        if (espActive || chamsActive) {
            // Return false = "not invisible" so the entity is rendered normally
            cir.setReturnValue(false);
        }
    }
}
