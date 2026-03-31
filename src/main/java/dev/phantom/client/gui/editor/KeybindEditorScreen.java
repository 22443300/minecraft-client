package dev.phantom.client.gui.editor;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.gui.theme.Theme;
import dev.phantom.client.render.Renderer2D;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/**
 * KeybindEditorScreen – view and edit keybinds for every registered module.
 *
 * <p>All modules are shown in a scrollable table.  Each row shows:
 * <ul>
 *   <li>A coloured category stripe on the left.</li>
 *   <li>The module name.</li>
 *   <li>The current keybind name (or "None" if unbound).</li>
 *   <li>A [Set] button that enters listening mode for that module.</li>
 *   <li>A [Clear] button that unsets the keybind.</li>
 * </ul>
 *
 * <p>While in listening mode the screen intercepts the next key press via
 * {@link #captureKey(int)} (called from {@code ScreenMixin}) and assigns it to
 * the target module, then exits listening mode.  Pressing ESC while listening
 * exits listening mode without changing the keybind (the bind must be cleared
 * explicitly with [Clear]).
 */
public class KeybindEditorScreen extends Screen {

    // -------------------------------------------------------------------------
    // Layout
    // -------------------------------------------------------------------------
    private static final int PANEL_MARGIN = 30;
    private static final int HEADER_H     = 30;
    private static final int ROW_H        = 22;
    private static final int PADDING      = 6;
    private static final int STRIPE_W     = 4;
    private static final int BTN_W        = 40;
    private static final int BTN_H        = 14;

    // Column X offsets relative to list area left
    private static final int COL_NAME     = STRIPE_W + 6;
    private static final int COL_KEY_OFF  = 200; // offset from left for keybind col
    private static final int COL_BTN_OFF  = 310; // offset from left for [Set]
    private static final int COL_CLR_OFF  = COL_BTN_OFF + BTN_W + 4; // [Clear]

    // -------------------------------------------------------------------------
    // State
    // -------------------------------------------------------------------------
    private List<Module> modules;
    private int scrollOffset = 0;
    private boolean listening = false;
    private Module listeningModule = null;

    public KeybindEditorScreen() {
        super(Text.literal("Keybind Editor"));
    }

    // -------------------------------------------------------------------------
    // Screen lifecycle
    // -------------------------------------------------------------------------

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        modules = PhantomClient.INSTANCE.modules.getModules();
    }

    // -------------------------------------------------------------------------
    // Listening API (called by ScreenMixin)
    // -------------------------------------------------------------------------

    /** Returns true when this screen is actively waiting for a key press. */
    public boolean isListening() {
        return listening;
    }

    /**
     * Called by {@code ScreenMixin} with the key code of the captured press.
     * Assigns the key to the listening module and exits listening mode.
     *
     * @param keyCode GLFW key constant
     */
    public void captureKey(int keyCode) {
        if (!listening || listeningModule == null) return;
        listeningModule.setKeybind(keyCode);
        listening       = false;
        listeningModule = null;
    }

    // -------------------------------------------------------------------------
    // Rendering
    // -------------------------------------------------------------------------

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, width, height, 0xAA000000);

        int panelX = PANEL_MARGIN;
        int panelY = PANEL_MARGIN;
        int panelW = width - PANEL_MARGIN * 2;
        int panelH = height - PANEL_MARGIN * 2;

        Renderer2D.drawRect(ctx, panelX, panelY, panelW, panelH, Theme.BACKGROUND);
        Renderer2D.drawOutlinedRect(ctx, panelX, panelY, panelW, panelH, Theme.BORDER, 0);

        // Header
        Renderer2D.drawRect(ctx, panelX, panelY, panelW, HEADER_H, Theme.HEADER);
        String title = listening
                ? "Keybind Editor  \u2013  Press a key to bind  (ESC = cancel)"
                : "Keybind Editor";
        ctx.drawText(textRenderer, title,
                panelX + PADDING, panelY + (HEADER_H - textRenderer.fontHeight) / 2,
                listening ? 0xFFFFEB3B : Theme.TEXT, true);

        // Column headers
        int listX = panelX + PADDING;
        int headY = panelY + HEADER_H + 2;
        ctx.drawText(textRenderer, "Module",
                listX + COL_NAME, headY, Theme.TEXT_DIM, false);
        ctx.drawText(textRenderer, "Key",
                listX + COL_KEY_OFF, headY, Theme.TEXT_DIM, false);

        // Rows
        int listY  = headY + textRenderer.fontHeight + 3;
        int listW  = panelW - PADDING * 2;
        int listH  = panelH - HEADER_H - (listY - (panelY + HEADER_H)) - PADDING;
        int visRows = listH / ROW_H;

        for (int i = scrollOffset; i < modules.size() && i < scrollOffset + visRows; i++) {
            Module m  = modules.get(i);
            int rowY  = listY + (i - scrollOffset) * ROW_H;
            boolean hov = mouseX >= listX && mouseX < listX + listW
                    && mouseY >= rowY && mouseY < rowY + ROW_H;

            boolean isTarget = (m == listeningModule);

            int bg = isTarget ? 0xFF1A2A3A
                   : hov      ? Theme.HOVER
                   :             Theme.MODULE_BG;
            Renderer2D.drawRect(ctx, listX, rowY, listW, ROW_H - 1, bg);

            // Category stripe
            Renderer2D.drawRect(ctx, listX, rowY, STRIPE_W, ROW_H - 1,
                    Theme.getCategoryColor(m.getCategory()));

            // Module name
            int textY = rowY + (ROW_H - textRenderer.fontHeight) / 2;
            ctx.drawText(textRenderer, m.getName(), listX + COL_NAME, textY,
                    m.isEnabled() ? Theme.TEXT : Theme.TEXT_DIM, false);

            // Keybind name
            String keyName = keyName(m.getKeybind());
            int keyColor   = isTarget ? 0xFF4A9EFF
                           : m.getKeybind() == GLFW.GLFW_KEY_UNKNOWN ? Theme.TEXT_DISABLED
                           : Theme.TEXT;
            ctx.drawText(textRenderer, keyName, listX + COL_KEY_OFF, textY, keyColor, false);

            // [Set] button
            int setX = listX + COL_BTN_OFF;
            int setY = rowY + (ROW_H - BTN_H) / 2;
            boolean setHov = mouseX >= setX && mouseX < setX + BTN_W
                    && mouseY >= setY && mouseY < setY + BTN_H;
            Renderer2D.drawRect(ctx, setX, setY, BTN_W, BTN_H,
                    setHov ? Theme.ACCENT : Theme.SIDEBAR);
            ctx.drawText(textRenderer, isTarget ? "..." : "Set",
                    setX + (BTN_W - textRenderer.getWidth(isTarget ? "..." : "Set")) / 2,
                    setY + (BTN_H - textRenderer.fontHeight) / 2,
                    isTarget ? 0xFF4A9EFF : Theme.TEXT, false);

            // [Clear] button
            int clrX = listX + COL_CLR_OFF;
            int clrY = setY;
            boolean clrHov = mouseX >= clrX && mouseX < clrX + BTN_W
                    && mouseY >= clrY && mouseY < clrY + BTN_H;
            Renderer2D.drawRect(ctx, clrX, clrY, BTN_W, BTN_H,
                    clrHov ? 0xFF3A1A1A : Theme.MODULE_BG);
            ctx.drawText(textRenderer, "Clear",
                    clrX + (BTN_W - textRenderer.getWidth("Clear")) / 2,
                    clrY + (BTN_H - textRenderer.fontHeight) / 2,
                    Theme.TEXT_DIM, false);
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        int mx = (int) mouseX;
        int my = (int) mouseY;

        int panelX = PANEL_MARGIN;
        int panelY = PANEL_MARGIN;
        int panelW = width - PANEL_MARGIN * 2;
        int panelH = height - PANEL_MARGIN * 2;
        int listX  = panelX + PADDING;
        int listY  = panelY + HEADER_H + textRenderer.fontHeight + 5 + 3;

        int visRows = (panelH - HEADER_H - PADDING) / ROW_H;

        for (int i = scrollOffset; i < modules.size() && i < scrollOffset + visRows; i++) {
            Module m  = modules.get(i);
            int rowY  = listY + (i - scrollOffset) * ROW_H;

            // [Set]
            int setX = listX + COL_BTN_OFF;
            int setY = rowY + (ROW_H - BTN_H) / 2;
            if (mx >= setX && mx < setX + BTN_W && my >= setY && my < setY + BTN_H) {
                if (listening && listeningModule == m) {
                    // Second click on same – cancel listening
                    listening       = false;
                    listeningModule = null;
                } else {
                    listening       = true;
                    listeningModule = m;
                }
                return true;
            }

            // [Clear]
            int clrX = listX + COL_CLR_OFF;
            int clrY = setY;
            if (mx >= clrX && mx < clrX + BTN_W && my >= clrY && my < clrY + BTN_H) {
                m.setKeybind(GLFW.GLFW_KEY_UNKNOWN);
                if (listeningModule == m) {
                    listening       = false;
                    listeningModule = null;
                }
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY,
                                 double horizontalAmount, double verticalAmount) {
        scrollOffset = Math.max(0,
                Math.min(scrollOffset - (int) verticalAmount, modules.size() - 1));
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (listening) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                // ESC cancels listening without changing the bind
                listening       = false;
                listeningModule = null;
                return true;
            }
            // All other keys are captured by ScreenMixin via captureKey()
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Returns a human-readable name for a GLFW key constant.
     *
     * @param keyCode GLFW key constant, or {@link GLFW#GLFW_KEY_UNKNOWN}
     * @return display string such as "F" or "LSHIFT"
     */
    private static String keyName(int keyCode) {
        if (keyCode == GLFW.GLFW_KEY_UNKNOWN) return "None";
        String name = GLFW.glfwGetKeyName(keyCode, 0);
        if (name != null && !name.isEmpty()) return name.toUpperCase();
        // GLFW returns null for non-printable keys; use constants for common ones
        return switch (keyCode) {
            case GLFW.GLFW_KEY_SPACE        -> "SPACE";
            case GLFW.GLFW_KEY_LEFT_SHIFT   -> "LSHIFT";
            case GLFW.GLFW_KEY_RIGHT_SHIFT  -> "RSHIFT";
            case GLFW.GLFW_KEY_LEFT_CONTROL -> "LCTRL";
            case GLFW.GLFW_KEY_RIGHT_CONTROL-> "RCTRL";
            case GLFW.GLFW_KEY_LEFT_ALT     -> "LALT";
            case GLFW.GLFW_KEY_RIGHT_ALT    -> "RALT";
            case GLFW.GLFW_KEY_TAB          -> "TAB";
            case GLFW.GLFW_KEY_ENTER        -> "ENTER";
            case GLFW.GLFW_KEY_ESCAPE       -> "ESC";
            case GLFW.GLFW_KEY_BACKSPACE    -> "BACKSPACE";
            case GLFW.GLFW_KEY_DELETE       -> "DELETE";
            case GLFW.GLFW_KEY_INSERT       -> "INSERT";
            case GLFW.GLFW_KEY_HOME         -> "HOME";
            case GLFW.GLFW_KEY_END          -> "END";
            case GLFW.GLFW_KEY_PAGE_UP      -> "PGUP";
            case GLFW.GLFW_KEY_PAGE_DOWN    -> "PGDN";
            case GLFW.GLFW_KEY_UP           -> "UP";
            case GLFW.GLFW_KEY_DOWN         -> "DOWN";
            case GLFW.GLFW_KEY_LEFT         -> "LEFT";
            case GLFW.GLFW_KEY_RIGHT        -> "RIGHT";
            default -> keyCode >= GLFW.GLFW_KEY_F1 && keyCode <= GLFW.GLFW_KEY_F12
                    ? "F" + (keyCode - GLFW.GLFW_KEY_F1 + 1)
                    : "KEY_" + keyCode;
        };
    }
}
