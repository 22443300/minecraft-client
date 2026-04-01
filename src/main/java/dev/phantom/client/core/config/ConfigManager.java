package dev.phantom.client.core.config;

import dev.phantom.client.core.event.EventBus;
import dev.phantom.client.core.event.EventHandler;
import dev.phantom.client.core.event.events.ConnectEvent;

public class ConfigManager {

    private final dev.phantom.client.core.module.ModuleManager moduleManager;
    private final dev.phantom.client.core.friend.FriendManager friendManager;

    public ConfigManager(
        dev.phantom.client.core.module.ModuleManager moduleManager,
        dev.phantom.client.core.friend.FriendManager friendManager
    ) {
        this.moduleManager = moduleManager;
        this.friendManager = friendManager;
        ProfileManager.setModuleManager(moduleManager);
        EventBus.INSTANCE.subscribe(this);
    }

    public void save() {
        saveFriends();
        ProfileManager.save();
    }

    public void load() {
        loadFriends();
        ProfileManager.load();
    }

    private void saveFriends() {
        try {
            java.nio.file.Path friendsFile = ProfileManager.getProfilesRoot()
                .resolve(ProfileManager.currentProfile)
                .resolve("friends.json");
            java.nio.file.Files.createDirectories(friendsFile.getParent());
            friendManager.saveToJson(friendsFile);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to save friends", e);
        }
    }

    private void loadFriends() {
        java.nio.file.Path friendsFile = ProfileManager.getProfilesRoot()
            .resolve(ProfileManager.currentProfile)
            .resolve("friends.json");
        if (java.nio.file.Files.exists(friendsFile)) {
            friendManager.loadFromJson(friendsFile);
        }
    }

    @EventHandler
    public void onConnect(ConnectEvent.Connect event) {
        load();
    }

    @EventHandler
    public void onDisconnect(ConnectEvent.Disconnect event) {
        save();
    }
}
