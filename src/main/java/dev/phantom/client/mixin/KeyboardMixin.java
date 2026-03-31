package dev.phantom.client.mixin;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.event.EventBus;
import dev.phantom.client.core.event.events.KeyEvent;
import dev.phantom.client.core.module.Module;
import net.minecraft.client.Keyboard;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    /**
     * Injects at the very beginning of the GLFW key callback.
     *
     * Responsibilities:
     *  1. Post a KeyEvent so that any subscriber (modules, macros, keybind
     *     editor) can react to every raw keypress/release/repeat.
     *  2. Toggle modules whose keybind matches the pressed key.
     *
     * The injection runs before vanilla processes the key so that a future
     * cancellable KeyEvent could suppress vanilla handling if needed.
     */
    @Inject(method = "onKey", at = @At("HEAD"))
    private void onKey(long window, int key, int scancode, int action, int modifiers,
                       CallbackInfo ci) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        // Post the raw key event for listeners (macros, keybind editor, etc.)
        EventBus.INSTANCE.post(new KeyEvent(key, action, modifiers));

        // Toggle modules on GLFW_PRESS only
        if (action == GLFW.GLFW_PRESS && key != GLFW.GLFW_KEY_UNKNOWN) {
            for (Module module : client.modules.getModules()) {
                if (module.getKeybind() == key) {
                    module.toggle();
                }
            }
        }
    }
}
