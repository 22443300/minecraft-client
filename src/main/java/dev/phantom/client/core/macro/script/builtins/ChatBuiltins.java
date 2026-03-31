package dev.phantom.client.core.macro.script.builtins;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.macro.script.Interpreter;
import dev.phantom.client.util.ChatUtil;
import net.minecraft.client.MinecraftClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class ChatBuiltins {

    private ChatBuiltins() {}

    public static void registerAll(Interpreter interp) {
        interp.registerBuiltin("send_chat", ChatBuiltins::sendChat);
        interp.registerBuiltin("send_command", ChatBuiltins::sendCommand);
    }

    // -------------------------------------------------------------------------
    // send_chat(message)
    // -------------------------------------------------------------------------

    private static Object sendChat(List<Object> args) throws Exception {
        if (args.isEmpty()) return null;
        String message = Interpreter.stringify(args.get(0));

        MinecraftClient mc = MinecraftClient.getInstance();
        CompletableFuture<Void> future = new CompletableFuture<>();
        mc.execute(() -> {
            try {
                ChatUtil.send(message);
                future.complete(null);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            PhantomClient.LOGGER.warn("[Phantom Macro] send_chat() failed: {}", e.getMessage());
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // send_command(command)
    // -------------------------------------------------------------------------

    private static Object sendCommand(List<Object> args) throws Exception {
        if (args.isEmpty()) return null;
        String command = Interpreter.stringify(args.get(0));

        MinecraftClient mc = MinecraftClient.getInstance();
        CompletableFuture<Void> future = new CompletableFuture<>();
        mc.execute(() -> {
            try {
                ChatUtil.sendCommand(command);
                future.complete(null);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            PhantomClient.LOGGER.warn("[Phantom Macro] send_command() failed: {}", e.getMessage());
        }
        return null;
    }
}
