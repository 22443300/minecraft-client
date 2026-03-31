package dev.phantom.client.mixin;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.event.EventBus;
import dev.phantom.client.core.event.events.KeyEvent;
import dev.phantom.client.gui.editor.KeybindEditorScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin targeting the base {@link Screen} class.
 *
 * <p>Intercepts {@code keyPressed} so that any open GUI screen can feed raw
 * key presses into the Phantom event bus. This is primarily used by the
 * keybind editor: when the editor is in "listening" mode it reads
 * {@link KeyEvent} from the bus and assigns the pressed key to the target
 * module without the key also triggering vanilla GUI shortcuts.
 */
@Mixin(Screen.class)
public class ScreenMixin {

    /**
     * Injected at HEAD of {@code keyPressed}, cancellable.
     *
     * <p>Posts a {@link KeyEvent} with {@code action = GLFW.GLFW_PRESS} so
     * that keybind-listening screens can capture the key. When the currently
     * open screen is a {@link KeybindEditorScreen} in listening mode, we
     * consume the key press and return {@code true} to suppress further
     * vanilla handling.
     *
     * @param keyCode   GLFW key code of the pressed key
     * @param scanCode  platform-specific scan code (passed through)
     * @param modifiers bitmask of active modifier keys (CTRL, SHIFT, ALT, …)
     * @param cir       return-value callback; set to {@code true} to signal
     *                  that the key was consumed by our handler
     */
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(int keyCode, int scanCode, int modifiers,
                              CallbackInfoReturnable<Boolean> cir) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        // Post PRESS-action key event into the bus so all listeners can react.
        KeyEvent event = new KeyEvent(keyCode, GLFW.GLFW_PRESS, modifiers);
        EventBus.INSTANCE.post(event);

        // If the current screen is the keybind editor and it is actively
        // listening for a key, let it capture this press and suppress vanilla.
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc != null && mc.currentScreen instanceof KeybindEditorScreen editor) {
            if (editor.isListening()) {
                editor.captureKey(keyCode);
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }
}
