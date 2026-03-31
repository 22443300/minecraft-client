package dev.phantom.client.integration;

import dev.phantom.client.util.ChatUtil;
import net.minecraft.util.math.Box;

public class WorldEditIntegration {
    private Box selectionBox = null;

    public void runCommand(String command) {
        String cmd = command.startsWith("/") ? command.substring(1) : command;
        ChatUtil.sendCommand(cmd);
    }

    public boolean hasActiveSelection() {
        try {
            // Try to access WorldEdit API
            Class.forName("com.sk89q.worldedit.fabric.FabricWorldEdit");
            return selectionBox != null;
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            return false;
        }
    }

    public Box getSelectionBox() { return selectionBox; }

    public void visualizeSelection() {
        try {
            // WorldEdit selection visualization - would use WE API here
            // Falls back gracefully if WE is not loaded correctly
        } catch (NoClassDefFoundError | Exception e) {
            // Silently ignore - WE may not be fully accessible
        }
    }
}
