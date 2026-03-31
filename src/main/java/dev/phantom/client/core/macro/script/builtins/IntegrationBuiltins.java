package dev.phantom.client.core.macro.script.builtins;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.macro.script.Interpreter;
import dev.phantom.client.integration.IntegrationManager;
import dev.phantom.client.integration.LitematicaIntegration;
import dev.phantom.client.integration.WorldEditIntegration;

import java.util.List;

public final class IntegrationBuiltins {

    private IntegrationBuiltins() {}

    public static void registerAll(Interpreter interp) {
        interp.registerBuiltin("worldedit", IntegrationBuiltins::worldEdit);
        interp.registerBuiltin("litematica", IntegrationBuiltins::litematica);
    }

    // -------------------------------------------------------------------------
    // worldedit(command)
    //   Sends a WorldEdit command as a slash command.
    //   E.g. worldedit("//copy") or worldedit("//paste -a")
    // -------------------------------------------------------------------------

    private static Object worldEdit(List<Object> args) throws Exception {
        if (args.isEmpty()) return null;
        String command = Interpreter.stringify(args.get(0));

        IntegrationManager integrations = PhantomClient.INSTANCE.integrations;
        if (!integrations.isWorldEditLoaded()) {
            PhantomClient.LOGGER.warn("[Phantom Macro] worldedit() called but WorldEdit is not loaded");
            return null;
        }

        WorldEditIntegration we = integrations.getWorldEdit();
        if (we != null) {
            // Ensure the command starts with // for WorldEdit commands
            if (!command.startsWith("/")) {
                command = "/" + command;
            }
            we.runCommand(command);
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // litematica(action)
    //   Supported actions: "toggle_easy_place", "summary"
    // -------------------------------------------------------------------------

    private static Object litematica(List<Object> args) throws Exception {
        if (args.isEmpty()) return null;
        String action = Interpreter.stringify(args.get(0)).toLowerCase();

        IntegrationManager integrations = PhantomClient.INSTANCE.integrations;
        if (!integrations.isLitematicaLoaded()) {
            PhantomClient.LOGGER.warn("[Phantom Macro] litematica() called but Litematica is not loaded");
            return null;
        }

        LitematicaIntegration lm = integrations.getLitematica();
        if (lm == null) return null;

        switch (action) {
            case "toggle_easy_place" -> lm.toggleEasyPlace();
            case "summary" -> {
                String summary = lm.getMaterialSummary();
                dev.phantom.client.util.ChatUtil.addMessage("Litematica: " + summary);
            }
            default -> PhantomClient.LOGGER.warn("[Phantom Macro] litematica(): unknown action '{}'", action);
        }
        return null;
    }
}
