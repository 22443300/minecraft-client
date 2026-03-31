package dev.phantom.client.gui.editor;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.config.ProfileManager;
import dev.phantom.client.core.macro.Macro;
import dev.phantom.client.core.macro.MacroManager;
import dev.phantom.client.gui.theme.Theme;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/**
 * Full-screen macro editor with syntax-highlighted script editing.
 *
 * <p>Layout:
 * <ul>
 *   <li>Left panel (200px) — macro list + New/Delete buttons</li>
 *   <li>Center panel — script text area with token-based syntax highlighting</li>
 *   <li>Right panel (160px) — macro properties (name, keybind, enabled)</li>
 *   <li>Bottom bar — Run, Save, Cancel buttons</li>
 * </ul>
 */
public class MacroEditorScreen extends Screen {

    // ── Layout constants ───────────────────────────────────────────────────
    private static final int PANEL_LEFT_W  = 200;
    private static final int PANEL_RIGHT_W = 160;
    private static final int BOTTOM_BAR_H  = 30;
    private static final int ITEM_H        = 20;

    // ── State ──────────────────────────────────────────────────────────────
    private final MacroManager macroManager;
    private Macro selectedMacro;

    /** The raw script text being edited (mutable copy). */
    private String scriptBuffer = "";

    /** Name field input buffer. */
    private String nameBuffer = "";

    /** Keybind display string (e.g. "NONE", "R", "F5"). */
    private String keybindDisplay = "NONE";

    /** Pending GLFW key code from rebind capture; -1 = not capturing. */
    private int capturingKeybind = -1;

    // ── Scroll offsets ────────────────────────────────────────────────────
    private int listScrollY = 0;
    private int scriptScrollY = 0;

    // ── Cursor / selection in script editor ───────────────────────────────
    private int cursorPos = 0;

    // ── Active text-focus field: 0=none, 1=name, 2=script ─────────────────
    private int focusedField = 0;

    public MacroEditorScreen() {
        super(Text.literal("Macro Editor"));
        this.macroManager = PhantomClient.INSTANCE.macros;
    }

    // ──────────────────────────────────────────────────────────────────────
    // Lifecycle
    // ──────────────────────────────────────────────────────────────────────

    @Override
    protected void init() {
        int lx = PANEL_LEFT_W;
        int rx = width - PANEL_RIGHT_W;
        int by = height - BOTTOM_BAR_H;

        // ── Bottom bar buttons ─────────────────────────────────────────────
        addDrawableChild(ButtonWidget.builder(Text.literal("Run"), b -> runMacro())
                .dimensions(lx + 4, by + 5, 50, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Save"), b -> saveMacro())
                .dimensions(lx + 60, by + 5, 50, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Close"), b -> close())
                .dimensions(lx + 116, by + 5, 50, 20).build());

        // ── Left panel — New / Delete ──────────────────────────────────────
        addDrawableChild(ButtonWidget.builder(Text.literal("+ New"), b -> newMacro())
                .dimensions(4, height - BOTTOM_BAR_H - 44, 90, 18).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Delete"), b -> deleteMacro())
                .dimensions(100, height - BOTTOM_BAR_H - 44, 96, 18).build());

        // ── Right panel — Set Keybind ──────────────────────────────────────
        addDrawableChild(ButtonWidget.builder(Text.literal("Set Key"), b -> startCapture())
                .dimensions(rx + 4, 60, PANEL_RIGHT_W - 8, 18).build());

        selectFirst();
    }

    private void selectFirst() {
        List<Macro> macros = macroManager.getMacros();
        if (!macros.isEmpty()) selectMacro(macros.get(0));
    }

    // ──────────────────────────────────────────────────────────────────────
    // Rendering
    // ──────────────────────────────────────────────────────────────────────

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Dark full background
        ctx.fill(0, 0, width, height, Theme.BACKGROUND);

        renderLeftPanel(ctx, mouseX, mouseY);
        renderScriptPanel(ctx, mouseX, mouseY);
        renderRightPanel(ctx, mouseX, mouseY);
        renderBottomBar(ctx);

        super.render(ctx, mouseX, mouseY, delta);
    }

    // ── Left panel ────────────────────────────────────────────────────────

    private void renderLeftPanel(DrawContext ctx, int mx, int my) {
        ctx.fill(0, 0, PANEL_LEFT_W, height, Theme.SIDEBAR);
        ctx.drawTextWithShadow(textRenderer, "Macros", 8, 8, Theme.TEXT);

        // Divider
        ctx.fill(0, 20, PANEL_LEFT_W, 21, Theme.BORDER);

        List<Macro> macros = macroManager.getMacros();
        int y = 24 - listScrollY;
        for (Macro macro : macros) {
            if (y + ITEM_H < 24 || y > height - BOTTOM_BAR_H - 46) {
                y += ITEM_H;
                continue;
            }
            boolean selected = macro == selectedMacro;
            boolean hovered  = mx >= 0 && mx < PANEL_LEFT_W && my >= y && my < y + ITEM_H;
            int bg = selected ? Theme.MODULE_ENABLED : (hovered ? Theme.HOVER : 0);
            if (bg != 0) ctx.fill(0, y, PANEL_LEFT_W, y + ITEM_H, bg);
            ctx.drawTextWithShadow(textRenderer,
                    macro.getName().length() > 22 ? macro.getName().substring(0, 22) : macro.getName(),
                    6, y + 6, macro.isEnabled() ? Theme.TEXT : Theme.TEXT_DIM);
            y += ITEM_H;
        }
    }

    // ── Script panel ─────────────────────────────────────────────────────

    private void renderScriptPanel(DrawContext ctx, int mx, int my) {
        int x0 = PANEL_LEFT_W;
        int x1 = width - PANEL_RIGHT_W;
        int y0 = 0;
        int y1 = height - BOTTOM_BAR_H;
        int padX = 8;
        int padY = 8;
        int lineH = textRenderer.fontHeight + 2;

        ctx.fill(x0, y0, x1, y1, 0xFF111111);
        ctx.fill(x0, y0, x0 + 1, y1, Theme.BORDER);
        ctx.fill(x1 - 1, y0, x1, y1, Theme.BORDER);

        ctx.drawTextWithShadow(textRenderer, "Script", x0 + padX, padY, Theme.TEXT);
        ctx.fill(x0, 18, x1, 19, Theme.BORDER);

        // Enable scissor for text region
        int textY0 = 22;
        int textY1 = y1 - 2;
        ctx.enableScissor(x0 + 1, textY0, x1 - 1, textY1);

        String[] lines = scriptBuffer.split("\n", -1);
        int drawY = textY0 + padY - scriptScrollY;
        for (String line : lines) {
            if (drawY + lineH > textY0 && drawY < textY1) {
                renderHighlightedLine(ctx, line, x0 + padX, drawY);
            }
            drawY += lineH;
        }

        ctx.disableScissor();
    }

    /**
     * Simple token-based syntax highlighting for the script language.
     * Colours: keywords=pink, builtins=cyan, strings=yellow, numbers=lightgreen, comments=darkgrey.
     */
    private void renderHighlightedLine(DrawContext ctx, String line, int x, int y) {
        int i = 0;
        int drawX = x;
        while (i < line.length()) {
            char c = line.charAt(i);

            // Comment
            if (c == '/' && i + 1 < line.length() && line.charAt(i + 1) == '/') {
                String rest = line.substring(i);
                ctx.drawTextWithShadow(textRenderer, rest, drawX, y, 0xFF666666);
                break;
            }

            // String literal
            if (c == '"') {
                int end = line.indexOf('"', i + 1);
                if (end == -1) end = line.length() - 1;
                String tok = line.substring(i, end + 1);
                ctx.drawTextWithShadow(textRenderer, tok, drawX, y, 0xFFE8C469);
                drawX += textRenderer.getWidth(tok);
                i = end + 1;
                continue;
            }

            // Number
            if (Character.isDigit(c) || (c == '-' && i + 1 < line.length() && Character.isDigit(line.charAt(i + 1)))) {
                int end = i + 1;
                while (end < line.length() && (Character.isDigit(line.charAt(end)) || line.charAt(end) == '.')) end++;
                String tok = line.substring(i, end);
                ctx.drawTextWithShadow(textRenderer, tok, drawX, y, 0xFF98C379);
                drawX += textRenderer.getWidth(tok);
                i = end;
                continue;
            }

            // Word (keyword or builtin or identifier)
            if (Character.isLetter(c) || c == '_') {
                int end = i + 1;
                while (end < line.length() && (Character.isLetterOrDigit(line.charAt(end)) || line.charAt(end) == '_')) end++;
                String word = line.substring(i, end);
                int color;
                if (isKeyword(word))  color = 0xFFC678DD;
                else if (isBuiltin(word)) color = 0xFF56B6C2;
                else color = Theme.TEXT;
                ctx.drawTextWithShadow(textRenderer, word, drawX, y, color);
                drawX += textRenderer.getWidth(word);
                i = end;
                continue;
            }

            // Operator / punctuation — light grey
            String ch = String.valueOf(c);
            ctx.drawTextWithShadow(textRenderer, ch, drawX, y, 0xFFABB2BF);
            drawX += textRenderer.getWidth(ch);
            i++;
        }
    }

    private static boolean isKeyword(String w) {
        return switch (w) {
            case "if", "else", "while", "loop", "var", "true", "false", "break", "continue" -> true;
            default -> false;
        };
    }

    private static boolean isBuiltin(String w) {
        return switch (w) {
            case "sleep", "jump", "sneak", "sprint", "use_item", "left_click", "right_click",
                    "swap_to_item", "swap_to_slot", "send_chat", "send_command",
                    "look_at", "look_at_nearest_player", "enable_module", "disable_module",
                    "key_press", "key_release", "worldedit", "litematica" -> true;
            default -> false;
        };
    }

    // ── Right panel ───────────────────────────────────────────────────────

    private void renderRightPanel(DrawContext ctx, int mx, int my) {
        int rx = width - PANEL_RIGHT_W;
        ctx.fill(rx, 0, width, height, Theme.SIDEBAR);
        ctx.fill(rx, 0, rx + 1, height, Theme.BORDER);

        ctx.drawTextWithShadow(textRenderer, "Properties", rx + 6, 8, Theme.TEXT);
        ctx.fill(rx, 20, width, 21, Theme.BORDER);

        if (selectedMacro == null) return;

        int y = 28;
        ctx.drawTextWithShadow(textRenderer, "Name:", rx + 6, y, Theme.TEXT_DIM);
        y += 12;
        boolean nameFocused = focusedField == 1;
        ctx.fill(rx + 4, y, width - 4, y + 14, nameFocused ? 0xFF1E2030 : 0xFF161820);
        ctx.fill(rx + 4, y, width - 4, y + 1, nameFocused ? Theme.ACCENT : Theme.BORDER);
        ctx.drawTextWithShadow(textRenderer, nameBuffer, rx + 7, y + 3, Theme.TEXT);
        y += 18;

        ctx.drawTextWithShadow(textRenderer, "Keybind:", rx + 6, y, Theme.TEXT_DIM);
        y += 12;
        boolean capturing = capturingKeybind != -1;
        ctx.fill(rx + 4, y, width - 4, y + 14, 0xFF161820);
        ctx.fill(rx + 4, y, width - 4, y + 1, capturing ? 0xFFFFAA00 : Theme.BORDER);
        String kbLabel = capturing ? "Press key..." : keybindDisplay;
        ctx.drawTextWithShadow(textRenderer, kbLabel, rx + 7, y + 3, capturing ? 0xFFFFAA00 : Theme.TEXT);
        y += 32; // button is placed below via addDrawableChild

        ctx.drawTextWithShadow(textRenderer, "Enabled:", rx + 6, y, Theme.TEXT_DIM);
        ctx.drawTextWithShadow(textRenderer,
                selectedMacro.isEnabled() ? "ON" : "OFF",
                rx + 70, y, selectedMacro.isEnabled() ? Theme.TOGGLE_ON : Theme.TOGGLE_OFF);
    }

    // ── Bottom bar ────────────────────────────────────────────────────────

    private void renderBottomBar(DrawContext ctx) {
        int by = height - BOTTOM_BAR_H;
        ctx.fill(0, by, width, height, Theme.SIDEBAR);
        ctx.fill(0, by, width, by + 1, Theme.BORDER);
        ctx.drawTextWithShadow(textRenderer,
                "Click list to select  |  Click script area to edit",
                width / 2 - 100, by + 11, Theme.TEXT_DIM);
    }

    // ──────────────────────────────────────────────────────────────────────
    // Mouse events
    // ──────────────────────────────────────────────────────────────────────

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        int ix = (int) mx, iy = (int) my;

        // Left panel — select macro
        if (ix < PANEL_LEFT_W && iy < height - BOTTOM_BAR_H - 46) {
            List<Macro> macros = macroManager.getMacros();
            int y = 24 - listScrollY;
            for (Macro macro : macros) {
                if (iy >= y && iy < y + ITEM_H) {
                    selectMacro(macro);
                    return true;
                }
                y += ITEM_H;
            }
        }

        // Script area — focus
        if (ix > PANEL_LEFT_W && ix < width - PANEL_RIGHT_W && iy < height - BOTTOM_BAR_H) {
            focusedField = 2;
            return true;
        }

        // Right panel — name field
        if (ix > width - PANEL_RIGHT_W) {
            int nameFieldY = 40;
            if (iy >= nameFieldY && iy < nameFieldY + 14) {
                focusedField = 1;
                return true;
            }
            // Toggle enabled
            int enabledY = 108;
            if (iy >= enabledY && iy < enabledY + 12 && selectedMacro != null) {
                selectedMacro.setEnabled(!selectedMacro.isEnabled());
                return true;
            }
            focusedField = 0;
        }

        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double hx, double vy) {
        int ix = (int) mx;
        int scrollDelta = (int) (-vy * 12);
        if (ix < PANEL_LEFT_W) {
            listScrollY = Math.max(0, listScrollY + scrollDelta);
        } else if (ix < width - PANEL_RIGHT_W) {
            scriptScrollY = Math.max(0, scriptScrollY + scrollDelta);
        }
        return true;
    }

    // ──────────────────────────────────────────────────────────────────────
    // Keyboard events
    // ──────────────────────────────────────────────────────────────────────

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Keybind capture
        if (capturingKeybind == 0) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                if (selectedMacro != null) selectedMacro.setKeybind(-1);
                keybindDisplay = "NONE";
            } else {
                if (selectedMacro != null) selectedMacro.setKeybind(keyCode);
                keybindDisplay = GLFW.glfwGetKeyName(keyCode, scanCode) != null
                        ? GLFW.glfwGetKeyName(keyCode, scanCode).toUpperCase()
                        : "KEY_" + keyCode;
            }
            capturingKeybind = -1;
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            close();
            return true;
        }

        // Name field typing
        if (focusedField == 1) {
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !nameBuffer.isEmpty()) {
                nameBuffer = nameBuffer.substring(0, nameBuffer.length() - 1);
                if (selectedMacro != null) selectedMacro.setName(nameBuffer);
                return true;
            }
        }

        // Script field nav
        if (focusedField == 2) {
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !scriptBuffer.isEmpty()) {
                scriptBuffer = scriptBuffer.substring(0, Math.max(0, cursorPos - 1))
                        + scriptBuffer.substring(cursorPos);
                cursorPos = Math.max(0, cursorPos - 1);
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                insertChar('\n');
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_TAB) {
                insertChar(' '); insertChar(' '); insertChar(' '); insertChar(' ');
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (focusedField == 1) {
            nameBuffer += chr;
            if (selectedMacro != null) selectedMacro.setName(nameBuffer);
            return true;
        }
        if (focusedField == 2) {
            insertChar(chr);
            return true;
        }
        return false;
    }

    private void insertChar(char c) {
        scriptBuffer = scriptBuffer.substring(0, cursorPos) + c + scriptBuffer.substring(cursorPos);
        cursorPos++;
    }

    // ──────────────────────────────────────────────────────────────────────
    // Actions
    // ──────────────────────────────────────────────────────────────────────

    private void newMacro() {
        Macro macro = new Macro("Macro " + (macroManager.getMacros().size() + 1), "", -1);
        macroManager.addMacro(macro);
        selectMacro(macro);
    }

    private void deleteMacro() {
        if (selectedMacro == null) return;
        macroManager.removeMacro(selectedMacro.getName());
        selectedMacro = null;
        scriptBuffer = "";
        nameBuffer = "";
        selectFirst();
    }

    private void saveMacro() {
        if (selectedMacro == null) return;
        selectedMacro.setScript(scriptBuffer);
        selectedMacro.setName(nameBuffer);
        macroManager.saveToJson(ProfileManager.getProfileDir().resolve("macros.json"));
    }

    private void runMacro() {
        if (selectedMacro == null) return;
        saveMacro();
        macroManager.runMacro(selectedMacro.getName());
    }

    private void selectMacro(Macro macro) {
        selectedMacro = macro;
        scriptBuffer = macro.getScript();
        nameBuffer = macro.getName();
        keybindDisplay = macro.getKeybind() == -1 ? "NONE"
                : (GLFW.glfwGetKeyName(macro.getKeybind(), 0) != null
                ? GLFW.glfwGetKeyName(macro.getKeybind(), 0).toUpperCase()
                : "KEY_" + macro.getKeybind());
        cursorPos = scriptBuffer.length();
        scriptScrollY = 0;
        focusedField = 0;
    }

    private void startCapture() {
        capturingKeybind = 0; // 0 = waiting for next key press
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
