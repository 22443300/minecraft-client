package dev.phantom.client.core.macro.script.builtins;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.macro.script.Interpreter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class InputBuiltins {

    private InputBuiltins() {}

    /**
     * Maps common key name strings to the corresponding MC KeyBinding option.
     * Returns null if no direct binding is available for the key name.
     */
    private static KeyBinding resolveKeyBinding(String keyName, MinecraftClient mc) {
        return switch (keyName.toLowerCase()) {
            case "forward", "w"     -> mc.options.forwardKey;
            case "back", "backward", "s" -> mc.options.backKey;
            case "left", "a"        -> mc.options.leftKey;
            case "right", "d"       -> mc.options.rightKey;
            case "jump", "space"    -> mc.options.jumpKey;
            case "sneak", "shift"   -> mc.options.sneakKey;
            case "sprint", "ctrl"   -> mc.options.sprintKey;
            case "attack", "lmb"    -> mc.options.attackKey;
            case "use", "rmb"       -> mc.options.useKey;
            case "pick_item", "mmb" -> mc.options.pickItemKey;
            case "drop", "q"        -> mc.options.dropKey;
            case "swap_hands", "f"  -> mc.options.swapHandsKey;
            case "inventory", "e"   -> mc.options.inventoryKey;
            case "chat", "t"        -> mc.options.chatKey;
            case "list_players", "tab" -> mc.options.playerListKey;
            case "screenshot", "f2" -> mc.options.screenshotKey;
            case "fullscreen", "f11" -> mc.options.fullscreenKey;
            case "perspective", "f5" -> mc.options.togglePerspectiveKey;
            case "smooth_camera"    -> mc.options.smoothCameraKey;
            case "zoom"             -> mc.options.saveToolbarActivatorKey; // fallback
            case "hotbar1", "1"     -> mc.options.hotbarKeys[0];
            case "hotbar2", "2"     -> mc.options.hotbarKeys[1];
            case "hotbar3", "3"     -> mc.options.hotbarKeys[2];
            case "hotbar4", "4"     -> mc.options.hotbarKeys[3];
            case "hotbar5", "5"     -> mc.options.hotbarKeys[4];
            case "hotbar6", "6"     -> mc.options.hotbarKeys[5];
            case "hotbar7", "7"     -> mc.options.hotbarKeys[6];
            case "hotbar8", "8"     -> mc.options.hotbarKeys[7];
            case "hotbar9", "9"     -> mc.options.hotbarKeys[8];
            default                 -> null;
        };
    }

    public static void registerAll(Interpreter interp) {
        interp.registerBuiltin("key_press", InputBuiltins::keyPress);
        interp.registerBuiltin("key_release", InputBuiltins::keyRelease);
    }

    // -------------------------------------------------------------------------
    // key_press(keyName)
    // -------------------------------------------------------------------------

    private static Object keyPress(List<Object> args) throws Exception {
        if (args.isEmpty()) return null;
        String keyName = args.get(0).toString();

        MinecraftClient mc = MinecraftClient.getInstance();
        CompletableFuture<Void> future = new CompletableFuture<>();
        mc.execute(() -> {
            try {
                KeyBinding binding = resolveKeyBinding(keyName, mc);
                if (binding != null) {
                    binding.setPressed(true);
                } else {
                    PhantomClient.LOGGER.warn("[Phantom Macro] key_press: unknown key name '{}'", keyName);
                }
                future.complete(null);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            PhantomClient.LOGGER.warn("[Phantom Macro] key_press() failed: {}", e.getMessage());
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // key_release(keyName)
    // -------------------------------------------------------------------------

    private static Object keyRelease(List<Object> args) throws Exception {
        if (args.isEmpty()) return null;
        String keyName = args.get(0).toString();

        MinecraftClient mc = MinecraftClient.getInstance();
        CompletableFuture<Void> future = new CompletableFuture<>();
        mc.execute(() -> {
            try {
                KeyBinding binding = resolveKeyBinding(keyName, mc);
                if (binding != null) {
                    binding.setPressed(false);
                } else {
                    PhantomClient.LOGGER.warn("[Phantom Macro] key_release: unknown key name '{}'", keyName);
                }
                future.complete(null);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            PhantomClient.LOGGER.warn("[Phantom Macro] key_release() failed: {}", e.getMessage());
        }
        return null;
    }
}
