package dev.phantom.client.core.macro.script.builtins;

import dev.phantom.client.core.macro.script.Interpreter;
import net.minecraft.client.MinecraftClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class ActionBuiltins {

    private ActionBuiltins() {}

    public static void registerAll(Interpreter interp) {
        interp.registerBuiltin("sleep", ActionBuiltins::sleep);
        interp.registerBuiltin("jump", args -> jump());
        interp.registerBuiltin("sneak", ActionBuiltins::sneak);
        interp.registerBuiltin("sprint", ActionBuiltins::sprint);
        interp.registerBuiltin("use_item", args -> useItem());
        interp.registerBuiltin("left_click", args -> leftClick());
        interp.registerBuiltin("right_click", args -> rightClick());
    }

    // -------------------------------------------------------------------------
    // sleep(ms)
    // -------------------------------------------------------------------------

    private static Object sleep(List<Object> args) throws InterruptedException {
        long ms = args.isEmpty() ? 0L : (long) Interpreter.toDouble(args.get(0));
        Thread.sleep(ms);
        return null;
    }

    // -------------------------------------------------------------------------
    // jump()
    // -------------------------------------------------------------------------

    private static Object jump() throws Exception {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return null;

        CompletableFuture<Void> future = new CompletableFuture<>();
        mc.execute(() -> {
            try {
                if (mc.player != null) {
                    mc.player.jump();
                }
                future.complete(null);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            dev.phantom.client.PhantomClient.LOGGER.warn("[Phantom Macro] jump() failed: {}", e.getMessage());
        }

        Thread.sleep(50);
        return null;
    }

    // -------------------------------------------------------------------------
    // sneak(boolean)
    // -------------------------------------------------------------------------

    private static Object sneak(List<Object> args) throws Exception {
        boolean state = args.isEmpty() ? true : Interpreter.isTruthy(args.get(0));

        MinecraftClient mc = MinecraftClient.getInstance();
        CompletableFuture<Void> future = new CompletableFuture<>();
        mc.execute(() -> {
            try {
                mc.options.sneakKey.setPressed(state);
                future.complete(null);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            dev.phantom.client.PhantomClient.LOGGER.warn("[Phantom Macro] sneak() failed: {}", e.getMessage());
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // sprint(boolean)
    // -------------------------------------------------------------------------

    private static Object sprint(List<Object> args) throws Exception {
        boolean state = args.isEmpty() ? true : Interpreter.isTruthy(args.get(0));

        MinecraftClient mc = MinecraftClient.getInstance();
        CompletableFuture<Void> future = new CompletableFuture<>();
        mc.execute(() -> {
            try {
                mc.options.sprintKey.setPressed(state);
                future.complete(null);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            dev.phantom.client.PhantomClient.LOGGER.warn("[Phantom Macro] sprint() failed: {}", e.getMessage());
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // use_item() / right_click()
    // -------------------------------------------------------------------------

    private static Object useItem() throws Exception {
        return rightClick();
    }

    private static Object rightClick() throws Exception {
        MinecraftClient mc = MinecraftClient.getInstance();
        CompletableFuture<Void> future = new CompletableFuture<>();
        mc.execute(() -> {
            try {
                if (mc.interactionManager != null && mc.player != null && mc.world != null) {
                    mc.options.useKey.setPressed(true);
                    // Simulate one right-click tick
                    mc.interactionManager.interactItem(mc.player, net.minecraft.util.Hand.MAIN_HAND);
                    mc.options.useKey.setPressed(false);
                }
                future.complete(null);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            dev.phantom.client.PhantomClient.LOGGER.warn("[Phantom Macro] right_click() failed: {}", e.getMessage());
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // left_click() — attack
    // -------------------------------------------------------------------------

    private static Object leftClick() throws Exception {
        MinecraftClient mc = MinecraftClient.getInstance();
        CompletableFuture<Void> future = new CompletableFuture<>();
        mc.execute(() -> {
            try {
                if (mc.interactionManager != null && mc.player != null) {
                    mc.options.attackKey.setPressed(true);
                    mc.interactionManager.attackBlock(
                            mc.player.getBlockPos(),
                            net.minecraft.util.math.Direction.UP
                    );
                    mc.options.attackKey.setPressed(false);
                }
                future.complete(null);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            dev.phantom.client.PhantomClient.LOGGER.warn("[Phantom Macro] left_click() failed: {}", e.getMessage());
        }
        return null;
    }
}
