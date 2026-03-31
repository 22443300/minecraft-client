package dev.phantom.client.mixin;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.event.EventBus;
import dev.phantom.client.core.event.events.PacketEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    /**
     * Intercepts every outgoing packet before it is handed to Netty.
     *
     * Modules can subscribe to PacketEvent.Send to:
     *  - Inspect packets (e.g. SpeedMine timing hacks).
     *  - Cancel packets by calling event.cancel() (e.g. PacketFly, PingSpoof).
     *  - Replace packets by swapping event.setPacket().
     *
     * When the event is cancelled the CallbackInfo is also cancelled so the
     * packet is never written to the network buffer.
     */
    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V",
            at = @At("HEAD"), cancellable = true)
    private void onSendPacket(Packet<?> packet, PacketCallbacks callbacks, CallbackInfo ci) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        PacketEvent.Send event = new PacketEvent.Send(packet);
        EventBus.INSTANCE.post(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
