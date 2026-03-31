package dev.phantom.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public final class ChatUtil {

    private ChatUtil() {}

    private static final String PREFIX = "§8[§6Phantom§8] §7";

    // -------------------------------------------------------------------------

    private static MinecraftClient mc() { return MinecraftClient.getInstance(); }

    // -------------------------------------------------------------------------

    /**
     * Sends a chat message to the server.
     */
    public static void send(String message) {
        MinecraftClient mc = mc();
        if (mc.player == null || mc.player.networkHandler == null) return;
        mc.player.networkHandler.sendChatMessage(message);
    }

    /**
     * Sends a command to the server. Leading slash is stripped automatically.
     */
    public static void sendCommand(String command) {
        MinecraftClient mc = mc();
        if (mc.player == null || mc.player.networkHandler == null) return;
        if (command.startsWith("/")) {
            command = command.substring(1);
        }
        mc.player.networkHandler.sendCommand(command);
    }

    /**
     * Adds a [Phantom] prefixed message to the local chat hud (not sent to server).
     */
    public static void addMessage(String message) {
        MinecraftClient mc = mc();
        if (mc.inGameHud == null) return;
        mc.inGameHud.getChatHud().addMessage(Text.literal(PREFIX + message));
    }

    /**
     * Adds a colored message to the local chat hud with a custom color code applied inline.
     * The color int is interpreted as an ARGB value; the message is prefixed with the color
     * using a hex color escape understood by Text.literal (§-format).
     */
    public static void addMessage(String message, int color) {
        // Build a hex color string (§x§r§r§g§g§b§b) for 1.16+ extended color codes
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8)  & 0xFF;
        int b =  color        & 0xFF;

        String hex = String.format("%02X%02X%02X", r, g, b);
        StringBuilder sb = new StringBuilder("§x");
        for (char c : hex.toCharArray()) {
            sb.append('§').append(c);
        }

        MinecraftClient mc = mc();
        if (mc.inGameHud == null) return;
        mc.inGameHud.getChatHud().addMessage(Text.literal(PREFIX + sb + message));
    }
}
