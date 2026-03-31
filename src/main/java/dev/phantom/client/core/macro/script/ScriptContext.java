package dev.phantom.client.core.macro.script;

import dev.phantom.client.core.macro.Macro;

import java.util.HashMap;
import java.util.Map;

public class ScriptContext {

    private final Map<String, Object> variables = new HashMap<>();
    private boolean shouldBreak = false;
    private boolean shouldContinue = false;
    private final Macro macro;

    public ScriptContext(Macro macro) {
        this.macro = macro;
    }

    // -------------------------------------------------------------------------
    // Variables
    // -------------------------------------------------------------------------

    public void setVar(String name, Object value) {
        variables.put(name, value);
    }

    public Object getVar(String name) {
        return variables.get(name);
    }

    public boolean hasVar(String name) {
        return variables.containsKey(name);
    }

    // -------------------------------------------------------------------------
    // Control flow flags
    // -------------------------------------------------------------------------

    public boolean isShouldBreak() {
        return shouldBreak;
    }

    public void setShouldBreak(boolean shouldBreak) {
        this.shouldBreak = shouldBreak;
    }

    public boolean isShouldContinue() {
        return shouldContinue;
    }

    public void setShouldContinue(boolean shouldContinue) {
        this.shouldContinue = shouldContinue;
    }

    // -------------------------------------------------------------------------
    // Macro reference
    // -------------------------------------------------------------------------

    public Macro getMacro() {
        return macro;
    }

    public String getMacroName() {
        return macro != null ? macro.getName() : "unknown";
    }
}
