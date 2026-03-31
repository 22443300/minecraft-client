package dev.phantom.client.mixin;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.event.EventBus;
import dev.phantom.client.core.event.events.AttackEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    /**
     * Posts AttackEvent before the vanilla attack logic executes.
     *
     * Modules can cancel this event to prevent any particular attack, which is
     * used by KillAura and AimAssist to gate their synthetic attacks, and by
     * friend-protection logic to block hits on whitelisted players.
     */
    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void onAttack(Entity target, CallbackInfo ci) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        AttackEvent event = new AttackEvent(target);
        EventBus.INSTANCE.post(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
