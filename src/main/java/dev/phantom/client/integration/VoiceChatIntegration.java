package dev.phantom.client.integration;

import dev.phantom.client.PhantomClient;

import java.util.HashSet;
import java.util.Set;

public class VoiceChatIntegration {

    /**
     * Local set of player names that have been muted.
     * This acts as a client-side record even if the voice chat API call fails.
     */
    private final Set<String> mutedPlayers = new HashSet<>();

    /**
     * Mutes a player in Simple Voice Chat.
     * The mute state is tracked locally and pushed to the voice chat API if available.
     *
     * @param playerName The player's username (case-insensitive)
     */
    public void mutePlayer(String playerName) {
        if (playerName == null || playerName.isBlank()) return;
        String key = playerName.toLowerCase();
        mutedPlayers.add(key);
        try {
            // Simple Voice Chat exposes a VoicechatClientApi that can be obtained via
            // VoicechatClient.CLIENT_API. We use reflection to avoid hard dependency.
            Class<?> clientClass = Class.forName("de.maxhenkel.voicechat.VoicechatClient");
            Object clientApi = clientClass.getField("CLIENT_API").get(null);
            if (clientApi != null) {
                // Find the player connection by username and mute it
                // The API shape varies by version; we fall back gracefully if the method is absent.
                try {
                    java.lang.reflect.Method muteMethod = clientApi.getClass()
                            .getMethod("mutePlayer", String.class);
                    muteMethod.invoke(clientApi, playerName);
                    PhantomClient.LOGGER.info("[Phantom] Voice Chat: muted '{}'", playerName);
                } catch (NoSuchMethodException e) {
                    PhantomClient.LOGGER.debug("[Phantom] Voice Chat mutePlayer API not found, stored locally");
                }
            }
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            // Voice chat not loaded - expected when absent
        } catch (Exception e) {
            PhantomClient.LOGGER.debug("[Phantom] VoiceChat mutePlayer failed: {}", e.getMessage());
        }
    }

    /**
     * Unmutes a player in Simple Voice Chat.
     *
     * @param playerName The player's username (case-insensitive)
     */
    public void unmutePlayer(String playerName) {
        if (playerName == null || playerName.isBlank()) return;
        String key = playerName.toLowerCase();
        mutedPlayers.remove(key);
        try {
            Class<?> clientClass = Class.forName("de.maxhenkel.voicechat.VoicechatClient");
            Object clientApi = clientClass.getField("CLIENT_API").get(null);
            if (clientApi != null) {
                try {
                    java.lang.reflect.Method unmuteMethod = clientApi.getClass()
                            .getMethod("unmutePlayer", String.class);
                    unmuteMethod.invoke(clientApi, playerName);
                    PhantomClient.LOGGER.info("[Phantom] Voice Chat: unmuted '{}'", playerName);
                } catch (NoSuchMethodException e) {
                    PhantomClient.LOGGER.debug("[Phantom] Voice Chat unmutePlayer API not found, removed locally");
                }
            }
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            // Voice chat not loaded
        } catch (Exception e) {
            PhantomClient.LOGGER.debug("[Phantom] VoiceChat unmutePlayer failed: {}", e.getMessage());
        }
    }

    /**
     * Returns true if the player has been muted via this integration.
     *
     * @param playerName The player's username (case-insensitive)
     */
    public boolean isPlayerMuted(String playerName) {
        if (playerName == null) return false;
        return mutedPlayers.contains(playerName.toLowerCase());
    }
}
