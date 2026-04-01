package dev.phantom.client.gui.editor;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.config.ProfileManager;
import dev.phantom.client.gui.theme.Theme;
import dev.phantom.client.render.Renderer2D;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * ProfileSwitcherScreen – manage configuration profiles.
 *
 * <p>Lists all profiles found under the {@code phantom/profiles/} config
 * directory.  The currently active profile is highlighted.  The player can
 * select a different profile, create a new one, or delete the selected one
 * (the {@code default} profile cannot be deleted).
 *
 * <p>Selecting a new profile calls {@link ProfileManager#setProfile(String)},
 * which saves the current state and loads the new one automatically.
 */
public class ProfileSwitcherScreen extends Screen {

    // -------------------------------------------------------------------------
    // Layout
    // -------------------------------------------------------------------------
    private static final int PANEL_MARGIN = 50;
    private static final int HEADER_H     = 30;
    private static final int FOOTER_H     = 50;
    private static final int ROW_H        = 24;
    private static final int PADDING      = 8;
    private static final int BTN_W        = 80;
    private static final int BTN_H        = 16;

    // -------------------------------------------------------------------------
    // State
    // -------------------------------------------------------------------------
    private List<String> profiles;
    private int selectedIndex = -1;
    private int scrollOffset  = 0;

    /** When non-null we are showing the "new profile name" input. */
    private TextFieldWidget newNameField;
    private boolean creatingNew = false;

    public ProfileSwitcherScreen() {
        super(Text.literal("Profiles"));
    }

    // -------------------------------------------------------------------------
    // Screen lifecycle
    // -------------------------------------------------------------------------

    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        profiles = ProfileManager.getProfiles();

        // Pre-select current profile
        String cur = ProfileManager.currentProfile;
        for (int i = 0; i < profiles.size(); i++) {
            if (profiles.get(i).equals(cur)) {
                selectedIndex = i;
                break;
            }
        }

        int panelX = PANEL_MARGIN;
        int panelW = width - PANEL_MARGIN * 2;
        int footerY = height - PANEL_MARGIN - FOOTER_H;

        newNameField = new TextFieldWidget(textRenderer,
                panelX + PADDING,
                footerY + (FOOTER_H - 16) / 2,
                panelW - BTN_W * 3 - PADDING * 4, 16,
                Text.literal("New profile name…"));
        newNameField.setMaxLength(32);
        newNameField.setPlaceholder(Text.literal("New profile name…"));
        newNameField.setVisible(false);
        addSelectableChild(newNameField);
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
        ctx.drawText(textRenderer, "Profiles",
                panelX + PADDING, panelY + (HEADER_H - textRenderer.fontHeight) / 2,
                Theme.TEXT, true);

        // List
        int listX  = panelX + PADDING;
        int listY  = panelY + HEADER_H + PADDING;
        int listW  = panelW - PADDING * 2;
        int listH  = panelH - HEADER_H - FOOTER_H - PADDING * 2;
        int visRows = listH / ROW_H;

        String cur = ProfileManager.currentProfile;
        for (int i = scrollOffset; i < profiles.size() && i < scrollOffset + visRows; i++) {
            String profile = profiles.get(i);
            int rowY = listY + (i - scrollOffset) * ROW_H;
            boolean selected  = (i == selectedIndex);
            boolean isCurrent = profile.equals(cur);
            boolean hovered   = mouseX >= listX && mouseX < listX + listW
                    && mouseY >= rowY && mouseY < rowY + ROW_H;

            int bg = selected  ? Theme.MODULE_ENABLED
                   : hovered   ? Theme.HOVER
                   :             Theme.MODULE_BG;
            Renderer2D.drawRect(ctx, listX, rowY, listW, ROW_H - 1, bg);

            // Active-profile marker
            if (isCurrent) {
                Renderer2D.drawRect(ctx, listX, rowY, 3, ROW_H - 1, Theme.TOGGLE_ON);
            }

            ctx.drawText(textRenderer, profile,
                    listX + 8, rowY + (ROW_H - textRenderer.fontHeight) / 2,
                    isCurrent ? Theme.TOGGLE_ON : Theme.TEXT, true);

            if (isCurrent) {
                ctx.drawText(textRenderer, "(active)",
                        listX + 8 + textRenderer.getWidth(profile) + 6,
                        rowY + (ROW_H - textRenderer.fontHeight) / 2,
                        Theme.TEXT_DIM, false);
            }
        }

        // Footer
        int footerY = panelY + panelH - FOOTER_H;
        Renderer2D.drawRect(ctx, panelX, footerY, panelW, FOOTER_H, Theme.HEADER);

        if (creatingNew) {
            newNameField.setVisible(true);
            newNameField.render(ctx, mouseX, mouseY, delta);
        } else {
            newNameField.setVisible(false);
        }

        // Buttons
        int btnY    = footerY + (FOOTER_H - BTN_H) / 2;
        int btnBase = panelX + panelW - PADDING;

        // [Delete]
        int delX = btnBase - BTN_W;
        drawButton(ctx, mouseX, mouseY, delX, btnY, BTN_W, BTN_H, "Delete",
                selectedIndex >= 0 && !profiles.get(selectedIndex).equals("default")
                        ? 0xFF3A1A1A : Theme.MODULE_BG,
                selectedIndex >= 0 && !profiles.get(selectedIndex).equals("default")
                        ? 0xFFF44336 : Theme.TEXT_DISABLED);

        // [Select]
        int selX = delX - BTN_W - PADDING;
        drawButton(ctx, mouseX, mouseY, selX, btnY, BTN_W, BTN_H, "Select",
                Theme.MODULE_BG, Theme.TEXT);

        // [New Profile] or [Create] / [Cancel]
        if (creatingNew) {
            // [Create]
            int creX = selX - BTN_W - PADDING;
            drawButton(ctx, mouseX, mouseY, creX, btnY, BTN_W, BTN_H, "Create",
                    0xFF1A3A1A, Theme.TOGGLE_ON);
            // [Cancel]
            int canX = creX - BTN_W - PADDING;
            drawButton(ctx, mouseX, mouseY, canX, btnY, BTN_W, BTN_H, "Cancel",
                    Theme.MODULE_BG, Theme.TEXT_DIM);
        } else {
            int newX = selX - BTN_W - PADDING;
            drawButton(ctx, mouseX, mouseY, newX, btnY, BTN_W, BTN_H, "New Profile",
                    Theme.MODULE_BG, Theme.TEXT);
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    private void drawButton(DrawContext ctx, int mx, int my,
                            int bx, int by, int bw, int bh,
                            String label, int bg, int fg) {
        boolean hov = mx >= bx && mx < bx + bw && my >= by && my < by + bh;
        int bgColor = hov ? brighten(bg) : bg;
        Renderer2D.drawRect(ctx, bx, by, bw, bh, bgColor);
        Renderer2D.drawOutlinedRect(ctx, bx, by, bw, bh, Theme.BORDER, 0);
        ctx.drawText(textRenderer, label,
                bx + (bw - textRenderer.getWidth(label)) / 2,
                by + (bh - textRenderer.fontHeight) / 2,
                fg, false);
    }

    private static int brighten(int color) {
        int a = (color >> 24) & 0xFF;
        int r = Math.min(255, ((color >> 16) & 0xFF) + 20);
        int g = Math.min(255, ((color >>  8) & 0xFF) + 20);
        int b = Math.min(255, ( color        & 0xFF) + 20);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    // -------------------------------------------------------------------------
    // Mouse interaction
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

        int footerY = panelY + panelH - FOOTER_H;
        int btnY    = footerY + (FOOTER_H - BTN_H) / 2;
        int btnBase = panelX + panelW - PADDING;

        // Delete
        int delX = btnBase - BTN_W;
        if (mx >= delX && mx < delX + BTN_W && my >= btnY && my < btnY + BTN_H) {
            deleteSelected();
            return true;
        }

        // Select
        int selX = delX - BTN_W - PADDING;
        if (mx >= selX && mx < selX + BTN_W && my >= btnY && my < btnY + BTN_H) {
            selectProfile();
            return true;
        }

        if (creatingNew) {
            int creX = selX - BTN_W - PADDING;
            if (mx >= creX && mx < creX + BTN_W && my >= btnY && my < btnY + BTN_H) {
                createNewProfile();
                return true;
            }
            int canX = creX - BTN_W - PADDING;
            if (mx >= canX && mx < canX + BTN_W && my >= btnY && my < btnY + BTN_H) {
                creatingNew = false;
                newNameField.setText("");
                return true;
            }
        } else {
            int newX = selX - BTN_W - PADDING;
            if (mx >= newX && mx < newX + BTN_W && my >= btnY && my < btnY + BTN_H) {
                creatingNew = true;
                newNameField.setFocused(true);
                return true;
            }
        }

        // Row click
        int listX  = panelX + PADDING;
        int listY  = panelY + HEADER_H + PADDING;
        int listW  = panelW - PADDING * 2;
        int listH  = panelH - HEADER_H - FOOTER_H - PADDING * 2;
        int visRows = listH / ROW_H;

        for (int i = scrollOffset; i < profiles.size() && i < scrollOffset + visRows; i++) {
            int rowY = listY + (i - scrollOffset) * ROW_H;
            if (mx >= listX && mx < listX + listW && my >= rowY && my < rowY + ROW_H) {
                selectedIndex = i;
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY,
                                 double horizontalAmount, double verticalAmount) {
        scrollOffset = Math.max(0, Math.min(scrollOffset - (int) verticalAmount,
                Math.max(0, profiles.size() - 1)));
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (creatingNew && newNameField.isFocused()) {
            if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER) {
                createNewProfile();
                return true;
            }
            if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
                creatingNew = false;
                newNameField.setText("");
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // -------------------------------------------------------------------------
    // Actions
    // -------------------------------------------------------------------------

    private void selectProfile() {
        if (selectedIndex < 0 || selectedIndex >= profiles.size()) return;
        String name = profiles.get(selectedIndex);
        ProfileManager.setProfile(name);
    }

    private void createNewProfile() {
        if (newNameField == null) return;
        String name = newNameField.getText().trim();
        if (name.isEmpty()) return;

        // Sanitise name: only alphanumeric, underscore, hyphen
        name = name.replaceAll("[^a-zA-Z0-9_\\-]", "_");

        try {
            Path dir = ProfileManager.CONFIG_DIR.resolve("profiles").resolve(name);
            Files.createDirectories(dir);
        } catch (Exception e) {
            PhantomClient.LOGGER.warn("[Phantom] Failed to create profile directory: {}", e.getMessage());
        }

        creatingNew = false;
        newNameField.setText("");
        profiles = ProfileManager.getProfiles();
        // Select the new profile
        for (int i = 0; i < profiles.size(); i++) {
            if (profiles.get(i).equals(name)) {
                selectedIndex = i;
                break;
            }
        }
        ProfileManager.setProfile(name);
    }

    private void deleteSelected() {
        if (selectedIndex < 0 || selectedIndex >= profiles.size()) return;
        String name = profiles.get(selectedIndex);
        if ("default".equals(name)) return; // never delete default

        try {
            Path dir = ProfileManager.CONFIG_DIR.resolve("profiles").resolve(name);
            if (Files.exists(dir)) {
                // Delete recursively
                try (var stream = Files.walk(dir)) {
                    stream.sorted(java.util.Comparator.reverseOrder())
                          .map(Path::toFile)
                          .forEach(java.io.File::delete);
                }
            }
        } catch (Exception e) {
            PhantomClient.LOGGER.warn("[Phantom] Failed to delete profile: {}", e.getMessage());
        }

        profiles = ProfileManager.getProfiles();
        selectedIndex = Math.max(0, selectedIndex - 1);

        // If we deleted the active profile, fall back to default
        if (ProfileManager.currentProfile.equals(name)) {
            ProfileManager.setProfile("default");
        }
    }
}
