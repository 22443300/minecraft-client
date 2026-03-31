package dev.phantom.client.core.config;

import com.google.gson.*;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.ModuleManager;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProfileManager {

    public static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("phantom");
    private static final Path PROFILES_DIR = CONFIG_DIR.resolve("profiles");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static String currentProfile = "default";

    private static ModuleManager moduleManager;

    public static void setModuleManager(ModuleManager manager) {
        moduleManager = manager;
    }

    public static void init() {
        try {
            Files.createDirectories(PROFILES_DIR.resolve("default"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create default profile directory", e);
        }
    }

    public static void save() {
        Path profileDir = PROFILES_DIR.resolve(currentProfile);
        try {
            Files.createDirectories(profileDir);
            saveModules(profileDir);
            saveHud(profileDir);
            saveFriends(profileDir);
            saveMacros(profileDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save profile: " + currentProfile, e);
        }
    }

    public static void load() {
        Path profileDir = PROFILES_DIR.resolve(currentProfile);
        if (!Files.exists(profileDir)) return;
        loadModules(profileDir);
        loadHud(profileDir);
        loadFriends(profileDir);
        loadMacros(profileDir);
    }

    private static void saveModules(Path profileDir) throws IOException {
        if (moduleManager == null) return;
        JsonObject root = new JsonObject();
        for (Module module : moduleManager.getModules()) {
            root.add(module.getName(), ModuleConfig.serialize(module));
        }
        writeJson(profileDir.resolve("modules.json"), root);
    }

    private static void loadModules(Path profileDir) {
        if (moduleManager == null) return;
        Path file = profileDir.resolve("modules.json");
        if (!Files.exists(file)) return;
        JsonObject root = readJson(file);
        if (root == null) return;
        for (Module module : moduleManager.getModules()) {
            if (root.has(module.getName())) {
                JsonObject obj = root.getAsJsonObject(module.getName());
                ModuleConfig.deserialize(module, obj);
            }
        }
    }

    private static void saveHud(Path profileDir) throws IOException {
        Path file = profileDir.resolve("hud.json");
        if (!Files.exists(file)) {
            writeJson(file, new JsonObject());
        }
    }

    private static void loadHud(Path profileDir) {
        // HUD loading handled by HudManager when available
    }

    private static void saveFriends(Path profileDir) throws IOException {
        Path file = profileDir.resolve("friends.json");
        if (!Files.exists(file)) {
            writeJson(file, new JsonArray());
        }
    }

    private static void loadFriends(Path profileDir) {
        // Friends loading handled by FriendManager when available
    }

    private static void saveMacros(Path profileDir) throws IOException {
        Path file = profileDir.resolve("macros.json");
        if (!Files.exists(file)) {
            writeJson(file, new JsonArray());
        }
    }

    private static void loadMacros(Path profileDir) {
        // Macros loading handled by MacroManager when available
    }

    public static List<String> getProfiles() {
        if (!Files.exists(PROFILES_DIR)) return Collections.singletonList("default");
        try (Stream<Path> stream = Files.list(PROFILES_DIR)) {
            return stream
                .filter(Files::isDirectory)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
        } catch (IOException e) {
            return Collections.singletonList("default");
        }
    }

    public static void setProfile(String name) {
        save();
        currentProfile = name;
        load();
    }

    public static Path getProfileDir() {
        return PROFILES_DIR.resolve(currentProfile);
    }

    public static Path getProfilesRoot() {
        return PROFILES_DIR;
    }

    private static void writeJson(Path path, JsonElement element) throws IOException {
        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(path), StandardCharsets.UTF_8)) {
            GSON.toJson(element, writer);
        }
    }

    private static JsonObject readJson(Path path) {
        try (Reader reader = new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8)) {
            JsonElement element = JsonParser.parseReader(reader);
            return element.isJsonObject() ? element.getAsJsonObject() : null;
        } catch (IOException e) {
            return null;
        }
    }
}
