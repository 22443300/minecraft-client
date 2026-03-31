package dev.phantom.client.gui.hud;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.config.ProfileManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.nio.file.Path;

/**
 * HudEditor – a full-screen overlay that lets the player drag HUD elements to
 * new positions.
 *
 * <p>While this screen is open the game continues to run (isPauseScreen returns
 * false). A dark semi-transparent overlay is drawn behind the elements so they
 * are easy to see. Each element gets a coloured outline: grey normally, amber
 * when the cursor is over it, and green when it is being dragged.
 *
 * <p>Closing the screen (ESC or the close button) saves all element positions
 * to the current profile's {@code hud.json}.
 */
public class HudEditor extends Screen {

    private final HudManager hudManager;

    private HudElement dragging = null;
    private int dragOffsetX;
    private int dragOffsetY;

    public HudEditor() {
        super(Text.literal("HUD Editor"));
        this.hudManager = PhantomClient.INSTANCE.hud;
    }

    // -------------------------------------------------------------------------
    // Screen contract
    // -------------------------------------------------------------------------

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        hudManager.setEditing(true);
    }

    // -------------------------------------------------------------------------
    // Rendering
    // -------------------------------------------------------------------------

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Semi-transparent dark overlay
        ctx.fill(0, 0, this.width, this.height, 0x88000000);

        // Render all visible HUD elements
        hudManager.render(ctx, delta);

        // Outline each element
        for (HudElement el : hudManager.getElements()) {
            int col;
            if (el == dragging) {
                col = 0xFF00FF00; // green – being dragged
            } else if (el.isHovered(mouseX, mouseY)) {
                col = 0xFFFFAA00; // amber – hovered
            } else {
                col = 0xFF888888; // grey – idle
            }
            ctx.drawBorder(el.getX() - 1, el.getY() - 1,
                           el.getWidth() + 2, el.getHeight() + 2, col);
        }

        // Header bar
        String header = "HUD Editor \u2013 Drag elements to reposition  |  ESC to close";
        int textW = textRenderer.getWidth(header);
        ctx.fill(0, 0, this.width, 14, 0xFF0D0D0D);
        ctx.drawText(textRenderer, header,
                (this.width - textW) / 2, 3, 0xFFAAAAAA, false);

        super.render(ctx, mouseX, mouseY, delta);
    }

    // -------------------------------------------------------------------------
    // Mouse interaction
    // -------------------------------------------------------------------------

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        int mx = (int) mouseX;
        int my = (int) mouseY;
        for (HudElement el : hudManager.getElements()) {
            if (el.isHovered(mx, my)) {
                dragging     = el;
                dragOffsetX  = mx - el.getX();
                dragOffsetY  = my - el.getY();
                el.onMousePress(mx, my);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button,
                                double deltaX, double deltaY) {
        if (dragging != null) {
            dragging.onMouseDrag((int) mouseX, (int) mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragging != null) {
            dragging.onMouseRelease();
            dragging = null;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    // -------------------------------------------------------------------------
    // Close handling
    // -------------------------------------------------------------------------

    @Override
    public void close() {
        hudManager.setEditing(false);
        // Persist HUD positions to the current profile directory
        try {
            Path hudPath = ProfileManager.CONFIG_DIR
                    .resolve("profiles")
                    .resolve(ProfileManager.currentProfile)
                    .resolve("hud.json");
            hudManager.savePositions(hudPath);
        } catch (Exception e) {
            PhantomClient.LOGGER.warn("[Phantom] HudEditor: failed to save positions", e);
        }
        super.close();
    }
}
