package dev.phantom.client.core.macro;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Macro {

    private String name;
    private String script;
    private int keybind = -1;
    private boolean enabled = true;
    private String description = "";

    public Macro(String name) {
        this.name = name;
        this.script = "";
    }

    public Macro(String name, String script) {
        this.name = name;
        this.script = script;
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public String getName() {
        return name;
    }

    public String getScript() {
        return script;
    }

    public int getKeybind() {
        return keybind;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getDescription() {
        return description;
    }

    // -------------------------------------------------------------------------
    // Setters
    // -------------------------------------------------------------------------

    public void setName(String name) {
        this.name = name;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public void setKeybind(int keybind) {
        this.keybind = keybind;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // -------------------------------------------------------------------------
    // JSON serialization
    // -------------------------------------------------------------------------

    public static Macro fromJson(JsonObject obj) {
        String name = obj.has("name") ? obj.get("name").getAsString() : "unnamed";
        String script = obj.has("script") ? obj.get("script").getAsString() : "";
        Macro macro = new Macro(name, script);
        if (obj.has("keybind")) {
            macro.setKeybind(obj.get("keybind").getAsInt());
        }
        if (obj.has("enabled")) {
            macro.setEnabled(obj.get("enabled").getAsBoolean());
        }
        if (obj.has("description")) {
            macro.setDescription(obj.get("description").getAsString());
        }
        return macro;
    }

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.add("name", new JsonPrimitive(name));
        obj.add("script", new JsonPrimitive(script));
        obj.add("keybind", new JsonPrimitive(keybind));
        obj.add("enabled", new JsonPrimitive(enabled));
        obj.add("description", new JsonPrimitive(description));
        return obj;
    }

    @Override
    public String toString() {
        return "Macro{name='" + name + "', keybind=" + keybind + ", enabled=" + enabled + "}";
    }
}
