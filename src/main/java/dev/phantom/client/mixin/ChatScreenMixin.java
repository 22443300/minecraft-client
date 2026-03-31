package dev.phantom.client.mixin;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.event.EventBus;
import dev.phantom.client.core.event.events.ChatEvent;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin targeting {@link ChatScreen}.
 *
 * <p>Intercepts outgoing chat messages before they are sent to the server so
 * that modules (e.g. command aliases, AutoCorrect, macro triggers) can inspect
 * or cancel them by posting a {@link ChatEvent.Send} through the event bus.
 */
@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    /**
     * Injected at HEAD of {@code sendMessage}.
     *
     * <p>Posts a {@link ChatEvent.Send} with the raw message string. If any
     * subscriber cancels the event the injection cancels the callback,
     * preventing the message from reaching the server.
     *
     * @param message      the text the player is about to send
     * @param addToHistory whether the message should be appended to the
     *                     client-side chat history
     * @param ci           mixin callback info – cancelled when the event is
     *                     cancelled by a subscriber
     */
    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    private void onSendMessage(String message, boolean addToHistory, CallbackInfo ci) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        ChatEvent.Send event = new ChatEvent.Send(message);
        EventBus.INSTANCE.post(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
