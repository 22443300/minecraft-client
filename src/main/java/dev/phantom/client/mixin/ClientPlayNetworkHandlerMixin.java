package dev.phantom.client.mixin;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.event.EventBus;
import dev.phantom.client.core.event.events.ConnectEvent;
import dev.phantom.client.core.module.Module;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    /**
     * Posts ConnectEvent.Connect when the server sends the Game Join packet,
     * which is the first packet received after a successful login.
     *
     * The server address is taken from the network handler's connection so
     * modules can log or react to which server the player joined.
     */
    @Inject(method = "onGameJoin", at = @At("TAIL"))
    private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        ClientPlayNetworkHandler self = (ClientPlayNetworkHandler) (Object) this;
        String address = self.getConnection().getAddress() != null
                ? self.getConnection().getAddress().toString()
                : "unknown";

        EventBus.INSTANCE.post(new ConnectEvent.Connect(address));
    }

    /**
     * Intercepts incoming entity velocity packets for the Velocity
     * (anti-knockback) module.
     *
     * Only the local player's velocity packets are relevant. If the module is
     * enabled the packet is cancelled entirely so the player receives zero
     * knockback. A more granular implementation would scale the velocity
     * components instead; that is delegated to the LivingEntityMixin
     * takeKnockback injection which handles the physics side.
     */
    @Inject(method = "onEntityVelocityUpdate", at = @At("HEAD"), cancellable = true)
    private void onEntityVelocityUpdate(EntityVelocityUpdateS2CPacket packet, CallbackInfo ci) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc == null || mc.player == null) return;

        // Only care about the local player's velocity
        if (packet.getEntityId() != mc.player.getId()) return;

        Module velocity = client.modules.get("velocity").orElse(null);
        if (velocity != null && velocity.isEnabled()) {
            ci.cancel();
        }
    }
}
