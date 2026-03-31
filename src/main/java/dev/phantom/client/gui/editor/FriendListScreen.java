package dev.phantom.client.gui.editor;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.config.ProfileManager;
import dev.phantom.client.core.friend.Friend;
import dev.phantom.client.core.friend.FriendManager;
import dev.phantom.client.core.friend.FriendRelation;
import dev.phantom.client.gui.theme.Theme;
import dev.phantom.client.render.Renderer2D;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * FriendListScreen – manage the friend/enemy list.
 *
 * <p>Layout:
 * <pre>
 *  ┌────────────────────────────────────────────────────┐
 *  │  Friends & Enemies                       [search ] │
 *  ├────────────────────────────────────────────────────┤
 *  │  • PlayerName    [Friend]  [Remove]                │
 *  │  • EnemyName     [Enemy ]  [Remove]                │
 *  │  …                                                 │
 *  ├────────────────────────────────────────────────────┤
 *  │  [ Add name field ]  [Add Friend] [Add Enemy]      │
 *  └────────────────────────────────────────────────────┘
 * </pre>
 */
public class FriendListScreen extends Screen {

    // -------------------------------------------------------------------------
    // Layout constants
    // -------------------------------------------------------------------------
    private static final int PANEL_MARGIN   = 30;
    private static final int HEADER_H       = 30;
    private static final int FOOTER_H       = 40;
    private static final int ROW_H          = 22;
    private static final int PADDING        = 6;
    private static final int BTN_W          = 70;
    private static final int BTN_H          = 14;
    private static final int SEARCH_W       = 120;

    // -------------------------------------------------------------------------
    // State
    // -------------------------------------------------------------------------
    private final FriendManager friends;
    private final List<Friend> displayed = new ArrayList<>();

    private TextFieldWidget searchField;
    private TextFieldWidget addNameField;
    private int scrollOffset = 0;

    // Buttons encoded as simple int arrays [x, y, w, h] for hit testing
    // – we handle them manually to avoid the vanilla button boilerplate.

    public FriendListScreen() {
        super(Text.literal("Friends & Enemies"));
        this.friends = PhantomClient.INSTANCE.friends;
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
        int panelX = PANEL_MARGIN;
        int panelW = width - PANEL_MARGIN * 2;

        // Search field (top-right of header)
        searchField = new TextFieldWidget(textRenderer,
                panelX + panelW - SEARCH_W - PADDING,
                PANEL_MARGIN + (HEADER_H - 16) / 2,
                SEARCH_W, 16,
                Text.literal("Search…"));
        searchField.setMaxLength(32);
        searchField.setPlaceholder(Text.literal("Search…"));
        addSelectableChild(searchField);

        // Add-name field (footer)
        int footerY = height - PANEL_MARGIN - FOOTER_H;
        addNameField = new TextFieldWidget(textRenderer,
                panelX + PADDING,
                footerY + (FOOTER_H - 16) / 2,
                panelW - BTN_W * 2 - PADDING * 4, 16,
                Text.literal("Player name…"));
        addNameField.setMaxLength(32);
        addNameField.setPlaceholder(Text.literal("Player name…"));
        addSelectableChild(addNameField);

        refreshList();
    }

    /** Rebuilds the displayed list from the search filter. */
    private void refreshList() {
        String query = searchField != null ? searchField.getText().toLowerCase() : "";
        displayed.clear();
        for (Friend f : friends.getAllFriends()) {
            if (query.isEmpty() || f.getName().toLowerCase().contains(query)) {
                displayed.add(f);
            }
        }
        scrollOffset = Math.max(0, Math.min(scrollOffset, displayed.size() - 1));
    }

    // -------------------------------------------------------------------------
    // Rendering
    // -------------------------------------------------------------------------

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Full-screen dim
        ctx.fill(0, 0, width, height, 0xAA000000);

        int panelX = PANEL_MARGIN;
        int panelY = PANEL_MARGIN;
        int panelW = width - PANEL_MARGIN * 2;
        int panelH = height - PANEL_MARGIN * 2;

        // Panel background
        Renderer2D.drawRect(ctx, panelX, panelY, panelW, panelH, Theme.BACKGROUND);
        Renderer2D.drawOutlinedRect(ctx, panelX, panelY, panelW, panelH, Theme.BORDER, 0x00000000);

        // Header
        Renderer2D.drawRect(ctx, panelX, panelY, panelW, HEADER_H, Theme.HEADER);
        ctx.drawText(textRenderer, "Friends & Enemies",
                panelX + PADDING, panelY + (HEADER_H - textRenderer.fontHeight) / 2,
                Theme.TEXT, true);

        // Search field
        searchField.render(ctx, mouseX, mouseY, delta);

        // List area
        int listX  = panelX + PADDING;
        int listY  = panelY + HEADER_H + PADDING;
        int listW  = panelW - PADDING * 2;
        int listH  = panelH - HEADER_H - FOOTER_H - PADDING * 2;
        int visRows = listH / ROW_H;

        int rowY = listY;
        for (int i = scrollOffset; i < displayed.size() && i < scrollOffset + visRows; i++) {
            Friend f = displayed.get(i);
            boolean hovered = mouseX >= listX && mouseX < listX + listW
                    && mouseY >= rowY && mouseY < rowY + ROW_H;

            Renderer2D.drawRect(ctx, listX, rowY, listW, ROW_H - 1,
                    hovered ? Theme.HOVER : Theme.MODULE_BG);

            // Relation colour dot
            int relColor = f.getRelation() == FriendRelation.FRIEND ? Theme.TOGGLE_ON
                         : f.getRelation() == FriendRelation.ENEMY  ? 0xFFF44336
                         : Theme.TEXT_DIM;
            Renderer2D.drawRect(ctx, listX + 3, rowY + ROW_H / 2 - 3, 6, 6, relColor);

            // Name
            ctx.drawText(textRenderer, f.getName(),
                    listX + 14, rowY + (ROW_H - textRenderer.fontHeight) / 2,
                    Theme.TEXT, true);

            // Relation label
            String relLabel = f.getRelation() == FriendRelation.FRIEND ? "Friend"
                            : f.getRelation() == FriendRelation.ENEMY  ? "Enemy"
                            : "Neutral";
            ctx.drawText(textRenderer, relLabel,
                    listX + 130, rowY + (ROW_H - textRenderer.fontHeight) / 2,
                    relColor, true);

            // [Toggle] button
            int togX = listX + listW - BTN_W * 2 - PADDING * 2;
            int togY = rowY + (ROW_H - BTN_H) / 2;
            boolean togHov = mouseX >= togX && mouseX < togX + BTN_W
                    && mouseY >= togY && mouseY < togY + BTN_H;
            Renderer2D.drawRect(ctx, togX, togY, BTN_W, BTN_H,
                    togHov ? Theme.ACCENT : Theme.SIDEBAR);
            ctx.drawText(textRenderer, "Toggle",
                    togX + (BTN_W - textRenderer.getWidth("Toggle")) / 2,
                    togY + (BTN_H - textRenderer.fontHeight) / 2, Theme.TEXT, false);

            // [Remove] button
            int remX = listX + listW - BTN_W - PADDING;
            int remY = togY;
            boolean remHov = mouseX >= remX && mouseX < remX + BTN_W
                    && mouseY >= remY && mouseY < remY + BTN_H;
            Renderer2D.drawRect(ctx, remX, remY, BTN_W, BTN_H,
                    remHov ? 0xFF8B0000 : 0xFF3A1A1A);
            ctx.drawText(textRenderer, "Remove",
                    remX + (BTN_W - textRenderer.getWidth("Remove")) / 2,
                    remY + (BTN_H - textRenderer.fontHeight) / 2, Theme.TEXT, false);

            rowY += ROW_H;
        }

        // Footer
        int footerY = panelY + panelH - FOOTER_H;
        Renderer2D.drawRect(ctx, panelX, footerY, panelW, FOOTER_H, Theme.HEADER);
        addNameField.render(ctx, mouseX, mouseY, delta);

        // [Add Friend] button
        int addFX = panelX + panelW - BTN_W * 2 - PADDING * 2;
        int addFY = footerY + (FOOTER_H - BTN_H) / 2;
        boolean addFHov = mouseX >= addFX && mouseX < addFX + BTN_W
                && mouseY >= addFY && mouseY < addFY + BTN_H;
        Renderer2D.drawRect(ctx, addFX, addFY, BTN_W, BTN_H,
                addFHov ? Theme.TOGGLE_ON : 0xFF1A3A1A);
        ctx.drawText(textRenderer, "Add Friend",
                addFX + (BTN_W - textRenderer.getWidth("Add Friend")) / 2,
                addFY + (BTN_H - textRenderer.fontHeight) / 2, Theme.TEXT, false);

        // [Add Enemy] button
        int addEX = panelX + panelW - BTN_W - PADDING;
        int addEY = addFY;
        boolean addEHov = mouseX >= addEX && mouseX < addEX + BTN_W
                && mouseY >= addEY && mouseY < addEY + BTN_H;
        Renderer2D.drawRect(ctx, addEX, addEY, BTN_W, BTN_H,
                addEHov ? 0xFFF44336 : 0xFF3A1A1A);
        ctx.drawText(textRenderer, "Add Enemy",
                addEX + (BTN_W - textRenderer.getWidth("Add Enemy")) / 2,
                addEY + (BTN_H - textRenderer.fontHeight) / 2, Theme.TEXT, false);

        super.render(ctx, mouseX, mouseY, delta);
    }

    // -------------------------------------------------------------------------
    // Input handling
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

        // Footer buttons
        int footerY = panelY + panelH - FOOTER_H;
        int addFX   = panelX + panelW - BTN_W * 2 - PADDING * 2;
        int addFY   = footerY + (FOOTER_H - BTN_H) / 2;
        int addEX   = panelX + panelW - BTN_W - PADDING;

        if (mx >= addFX && mx < addFX + BTN_W && my >= addFY && my < addFY + BTN_H) {
            addEntry(FriendRelation.FRIEND);
            return true;
        }
        if (mx >= addEX && mx < addEX + BTN_W && my >= addFY && my < addFY + BTN_H) {
            addEntry(FriendRelation.ENEMY);
            return true;
        }

        // List row buttons
        int listX  = panelX + PADDING;
        int listY  = panelY + HEADER_H + PADDING;
        int listW  = panelW - PADDING * 2;
        int visRows = (panelH - HEADER_H - FOOTER_H - PADDING * 2) / ROW_H;

        for (int i = scrollOffset; i < displayed.size() && i < scrollOffset + visRows; i++) {
            int rowY = listY + (i - scrollOffset) * ROW_H;

            // Toggle button
            int togX = listX + listW - BTN_W * 2 - PADDING * 2;
            int togY = rowY + (ROW_H - BTN_H) / 2;
            if (mx >= togX && mx < togX + BTN_W && my >= togY && my < togY + BTN_H) {
                Friend f = displayed.get(i);
                FriendRelation next = f.getRelation() == FriendRelation.FRIEND
                        ? FriendRelation.ENEMY : FriendRelation.FRIEND;
                f.setRelation(next);
                refreshList();
                return true;
            }

            // Remove button
            int remX = listX + listW - BTN_W - PADDING;
            int remY = togY;
            if (mx >= remX && mx < remX + BTN_W && my >= remY && my < remY + BTN_H) {
                friends.removeFriend(displayed.get(i).getName());
                refreshList();
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount,
                                 double verticalAmount) {
        scrollOffset = Math.max(0, Math.min(scrollOffset - (int) verticalAmount,
                Math.max(0, displayed.size() - 1)));
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searchField.isFocused() || addNameField.isFocused()) {
            boolean handled = super.keyPressed(keyCode, scanCode, modifiers);
            refreshList();
            return handled;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        boolean handled = super.charTyped(chr, modifiers);
        refreshList();
        return handled;
    }

    // -------------------------------------------------------------------------
    // Close
    // -------------------------------------------------------------------------

    @Override
    public void close() {
        // Persist changes
        try {
            Path path = ProfileManager.CONFIG_DIR
                    .resolve("profiles")
                    .resolve(ProfileManager.currentProfile)
                    .resolve("friends.json");
            friends.saveToJson(path);
        } catch (Exception e) {
            PhantomClient.LOGGER.warn("[Phantom] FriendListScreen: failed to save", e);
        }
        super.close();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void addEntry(FriendRelation relation) {
        if (addNameField == null) return;
        String name = addNameField.getText().trim();
        if (name.isEmpty()) return;
        friends.addFriend(name, relation);
        addNameField.setText("");
        refreshList();
    }
}
