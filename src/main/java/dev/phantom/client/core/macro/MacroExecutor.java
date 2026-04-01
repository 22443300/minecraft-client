package dev.phantom.client.core.macro;

import dev.phantom.client.core.macro.script.Interpreter;
import dev.phantom.client.core.macro.script.Lexer;
import dev.phantom.client.core.macro.script.Parser;
import dev.phantom.client.core.macro.script.ScriptContext;
import dev.phantom.client.core.macro.script.ASTNode;
import dev.phantom.client.util.ChatUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MacroExecutor {

    public static final MacroExecutor INSTANCE = new MacroExecutor();

    private ExecutorService executor = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "PhantomMacro");
        t.setDaemon(true);
        return t;
    });

    private final Map<String, Future<?>> activeFutures = new ConcurrentHashMap<>();

    private MacroExecutor() {}

    /**
     * Submits the macro script for execution on a daemon thread.
     */
    public void execute(Macro macro, ScriptContext ctx) {
        // Cancel any existing run of the same macro
        Future<?> existing = activeFutures.get(macro.getName());
        if (existing != null && !existing.isDone()) {
            existing.cancel(true);
        }

        Future<?> future = executor.submit(() -> {
            try {
                Lexer lexer = new Lexer();
                var tokens = lexer.tokenize(macro.getScript());

                Parser parser = new Parser(tokens);
                ASTNode.Program program = parser.parseProgram();

                Interpreter interpreter = new Interpreter(ctx);
                interpreter.execute(program);
            } catch (dev.phantom.client.core.macro.script.Parser.ParseException e) {
                ChatUtil.addMessage("§cMacro parse error in '" + macro.getName() + "': " + e.getMessage());
                dev.phantom.client.PhantomClient.LOGGER.error("[Phantom] Macro parse error: {}", e.getMessage());
            } catch (dev.phantom.client.core.macro.script.Interpreter.ScriptRuntimeException e) {
                ChatUtil.addMessage("§cMacro runtime error in '" + macro.getName() + "': " + e.getMessage());
                dev.phantom.client.PhantomClient.LOGGER.error("[Phantom] Macro runtime error: {}", e.getMessage());
            } catch (Exception e) {
                ChatUtil.addMessage("§cMacro error in '" + macro.getName() + "': " + e.getMessage());
                dev.phantom.client.PhantomClient.LOGGER.error("[Phantom] Macro error in '{}': {}", macro.getName(), e.getMessage());
            } finally {
                activeFutures.remove(macro.getName());
            }
        });

        activeFutures.put(macro.getName(), future);
    }

    /**
     * Returns true if a macro with the given name is currently running.
     */
    public boolean isRunning(String macroName) {
        Future<?> future = activeFutures.get(macroName);
        return future != null && !future.isDone();
    }

    /**
     * Stops all running macro threads and recreates the executor.
     */
    public void stopAll() {
        executor.shutdownNow();
        activeFutures.clear();
        executor = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "PhantomMacro");
            t.setDaemon(true);
            return t;
        });
    }
}
