package dev.phantom.client.core.friend;

import com.google.gson.*;
import net.minecraft.entity.player.PlayerEntity;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class FriendManager {

    private final List<Friend> friends = new ArrayList<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public void addFriend(String name, FriendRelation relation) {
        if (getFriend(name).isEmpty()) {
            friends.add(new Friend(name, relation));
        } else {
            getFriend(name).ifPresent(f -> f.setRelation(relation));
        }
    }

    public void removeFriend(String name) {
        friends.removeIf(f -> f.getName().equalsIgnoreCase(name));
    }

    public boolean isFriend(String name) {
        return getFriend(name).map(f -> f.getRelation() == FriendRelation.FRIEND).orElse(false);
    }

    public boolean isEnemy(String name) {
        return getFriend(name).map(f -> f.getRelation() == FriendRelation.ENEMY).orElse(false);
    }

    public Optional<Friend> getFriend(String name) {
        return friends.stream()
            .filter(f -> f.getName().equalsIgnoreCase(name))
            .findFirst();
    }

    public List<Friend> getFriendList() {
        return friends.stream()
            .filter(f -> f.getRelation() == FriendRelation.FRIEND)
            .collect(Collectors.toList());
    }

    public List<Friend> getEnemyList() {
        return friends.stream()
            .filter(f -> f.getRelation() == FriendRelation.ENEMY)
            .collect(Collectors.toList());
    }

    public boolean isFriendEntity(PlayerEntity entity) {
        if (entity == null) return false;
        String name = entity.getName().getString();
        return isFriend(name);
    }

    public void saveToJson(Path path) throws IOException {
        JsonArray array = new JsonArray();
        for (Friend friend : friends) {
            array.add(friend.serialize());
        }
        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(path), StandardCharsets.UTF_8)) {
            GSON.toJson(array, writer);
        }
    }

    public void loadFromJson(Path path) {
        friends.clear();
        try (Reader reader = new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8)) {
            JsonElement element = JsonParser.parseReader(reader);
            if (!element.isJsonArray()) return;
            JsonArray array = element.getAsJsonArray();
            for (JsonElement entry : array) {
                if (entry.isJsonObject()) {
                    friends.add(Friend.deserialize(entry.getAsJsonObject()));
                }
            }
        } catch (IOException e) {
            // No existing friends file is fine
        }
    }

    public List<Friend> getAllFriends() {
        return Collections.unmodifiableList(friends);
    }
}
