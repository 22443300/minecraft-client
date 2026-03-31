package dev.phantom.client.core.macro.script.builtins;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.macro.script.Interpreter;
import net.minecraft.client.MinecraftClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class ModuleBuiltins {

    private ModuleBuiltins() {}

    public static void registerAll(Interpreter interp) {
        interp.registerBuiltin("enable_module", ModuleBuiltins::enableModule);
        interp.registerBuiltin("disable_module", ModuleBuiltins::disableModule);
    }

    // -------------------------------------------------------------------------
    // enable_module(name)
    // -------------------------------------------------------------------------

    private static Object enableModule(List<Object> args) throws Exception {
        if (args.isEmpty()) return null;
        String name = args.get(0).toString();

        MinecraftClient mc = MinecraftClient.getInstance();
        CompletableFuture<Void> future = new CompletableFuture<>();
        mc.execute(() -> {
            try {
                PhantomClient.INSTANCE.modules.get(name).ifPresent(module -> {
                    if (!module.isEnabled()) {
                        module.toggle();
                    }
                });
                future.complete(null);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            PhantomClient.LOGGER.warn("[Phantom Macro] enable_module() failed: {}", e.getMessage());
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // disable_module(name)
    // -------------------------------------------------------------------------

    private static Object disableModule(List<Object> args) throws Exception {
        if (args.isEmpty()) return null;
        String name = args.get(0).toString();

        MinecraftClient mc = MinecraftClient.getInstance();
        CompletableFuture<Void> future = new CompletableFuture<>();
        mc.execute(() -> {
            try {
                PhantomClient.INSTANCE.modules.get(name).ifPresent(module -> {
                    if (module.isEnabled()) {
                        module.toggle();
                    }
                });
                future.complete(null);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            PhantomClient.LOGGER.warn("[Phantom Macro] disable_module() failed: {}", e.getMessage());
        }
        return null;
    }
}
