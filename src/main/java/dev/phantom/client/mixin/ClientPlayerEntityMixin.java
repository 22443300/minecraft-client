package dev.phantom.client.mixin;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.event.EventBus;
import dev.phantom.client.core.event.events.MoveEvent;
import dev.phantom.client.core.module.Module;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    /**
     * Injects at the head of travel() to post a MoveEvent with the player's
     * current velocity. Modules can cancel the event to suppress movement or
     * modify the x/y/z fields to redirect it.
     *
     * Also handles NoClip: if that module is enabled the method is cancelled
     * entirely so vanilla collision-based movement is skipped.
     */
    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void onTravel(Vec3d movementInput, CallbackInfo ci) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        // NoClip: cancel normal travel so the entity passes through blocks
        Module noClip = client.modules.get("noclip").orElse(null);
        if (noClip != null && noClip.isEnabled()) {
            ci.cancel();
            return;
        }

        ClientPlayerEntity self = (ClientPlayerEntity) (Object) this;
        Vec3d vel = self.getVelocity();
        MoveEvent event = new MoveEvent(vel.x, vel.y, vel.z);
        EventBus.INSTANCE.post(event);

        if (event.isCancelled()) {
            ci.cancel();
            return;
        }

        // Apply any modifications modules made to the event back to the velocity
        if (event.getX() != vel.x || event.getY() != vel.y || event.getZ() != vel.z) {
            self.setVelocity(event.getX(), event.getY(), event.getZ());
        }
    }

    /**
     * Cancels pushOutOfBlocks when NoClip is active, preventing the engine
     * from correcting the player's position while clipping through terrain.
     */
    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    private void onPushOutOfBlocks(double x, double z, CallbackInfo ci) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;
        Module noClip = client.modules.get("noclip").orElse(null);
        if (noClip != null && noClip.isEnabled()) {
            ci.cancel();
        }
    }
}
