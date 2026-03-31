package dev.phantom.client.core.macro;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.phantom.client.core.event.EventBus;
import dev.phantom.client.core.event.EventHandler;
import dev.phantom.client.core.event.events.KeyEvent;
import dev.phantom.client.core.macro.script.ScriptContext;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MacroManager {

    private final List<Macro> macros = new ArrayList<>();
    private final Map<Integer, Macro> keybindMap = new HashMap<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public MacroManager() {
        EventBus.INSTANCE.subscribe(this);
    }

    // -------------------------------------------------------------------------
    // Macro management
    // -------------------------------------------------------------------------

    public void addMacro(Macro macro) {
        // Remove any existing macro with the same name
        macros.removeIf(m -> m.getName().equalsIgnoreCase(macro.getName()));
        macros.add(macro);
        if (macro.getKeybind() != -1) {
            keybindMap.put(macro.getKeybind(), macro);
        }
    }

    public void removeMacro(String name) {
        macros.stream()
                .filter(m -> m.getName().equalsIgnoreCase(name))
                .findFirst()
                .ifPresent(macro -> {
                    macros.remove(macro);
                    if (macro.getKeybind() != -1) {
                        keybindMap.remove(macro.getKeybind());
                    }
                });
    }

    public Optional<Macro> getMacro(String name) {
        return macros.stream()
                .filter(m -> m.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public List<Macro> getMacros() {
        return Collections.unmodifiableList(macros);
    }

    // -------------------------------------------------------------------------
    // Running macros
    // -------------------------------------------------------------------------

    public void runMacro(String name) {
        getMacro(name).ifPresent(this::runMacro);
    }

    public void runMacro(Macro macro) {
        if (!macro.isEnabled()) return;
        ScriptContext ctx = new ScriptContext(macro);
        MacroExecutor.INSTANCE.execute(macro, ctx);
    }

    // -------------------------------------------------------------------------
    // Keybind handling
    // -------------------------------------------------------------------------

    public void onKeyPress(int key) {
        Macro macro = keybindMap.get(key);
        if (macro != null && macro.isEnabled()) {
            runMacro(macro);
        }
    }

    @EventHandler
    public void onKey(KeyEvent event) {
        if (event.isPress()) {
            onKeyPress(event.getKey());
        }
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    public void saveToJson(Path path) {
        JsonArray array = new JsonArray();
        for (Macro macro : macros) {
            array.add(macro.toJson());
        }
        JsonObject root = new JsonObject();
        root.add("macros", array);
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(root));
        } catch (IOException e) {
            dev.phantom.client.PhantomClient.LOGGER.error("[Phantom] Failed to save macros: {}", e.getMessage());
        }
    }

    public void loadFromJson(Path path) {
        if (!Files.exists(path)) return;
        try {
            String content = Files.readString(path);
            JsonObject root = JsonParser.parseString(content).getAsJsonObject();
            JsonArray array = root.getAsJsonArray("macros");
            macros.clear();
            keybindMap.clear();
            for (int i = 0; i < array.size(); i++) {
                Macro macro = Macro.fromJson(array.get(i).getAsJsonObject());
                macros.add(macro);
                if (macro.getKeybind() != -1) {
                    keybindMap.put(macro.getKeybind(), macro);
                }
            }
        } catch (IOException e) {
            dev.phantom.client.PhantomClient.LOGGER.error("[Phantom] Failed to load macros: {}", e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    public void stop() {
        MacroExecutor.INSTANCE.stopAll();
    }

    // -------------------------------------------------------------------------
    // Keybind map maintenance
    // -------------------------------------------------------------------------

    /**
     * Rebuilds the keybind map from the current macro list.
     * Call this after modifying a macro's keybind externally.
     */
    public void rebuildKeybindMap() {
        keybindMap.clear();
        for (Macro macro : macros) {
            if (macro.getKeybind() != -1) {
                keybindMap.put(macro.getKeybind(), macro);
            }
        }
    }
}
