package dev.phantom.client.mixin;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.event.EventBus;
import dev.phantom.client.core.event.events.ConnectEvent;
import dev.phantom.client.core.event.events.TickEvent;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    /**
     * Fires TickEvent at the end of every client tick.
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;
        EventBus.INSTANCE.post(new TickEvent());
    }

    /**
     * Fires ConnectEvent.Disconnect when the client disconnects from a server.
     * This covers the case where disconnect() is called directly on MinecraftClient.
     */
    @Inject(method = "disconnect()V", at = @At("HEAD"), require = 0)
    private void onDisconnect(CallbackInfo ci) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;
        EventBus.INSTANCE.post(new ConnectEvent.Disconnect());
    }
}
