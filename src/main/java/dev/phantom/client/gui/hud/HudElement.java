package dev.phantom.client.gui.hud;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.DrawContext;

/**
 * Abstract base class for all HUD overlay elements.
 *
 * Each concrete element must implement {@link #render(DrawContext, float)} and
 * {@link #updateSize()} so the {@link HudManager} can lay out and draw all
 * elements during the HUD render pass.
 */
public abstract class HudElement {

    // -------------------------------------------------------------------------
    // Identity
    // -------------------------------------------------------------------------
    protected final String id;
    protected final String name;

    // -------------------------------------------------------------------------
    // Position / Size
    // -------------------------------------------------------------------------
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    // -------------------------------------------------------------------------
    // Visibility / Drag state
    // -------------------------------------------------------------------------
    protected boolean visible   = true;
    protected boolean dragging  = false;
    protected int dragOffsetX   = 0;
    protected int dragOffsetY   = 0;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    protected HudElement(String id, String name, int defaultX, int defaultY) {
        this.id   = id;
        this.name = name;
        this.x    = defaultX;
        this.y    = defaultY;
        updateSize();
    }

    // -------------------------------------------------------------------------
    // Abstract interface
    // -------------------------------------------------------------------------

    /**
     * Renders this HUD element at its current {@link #x}/{@link #y} position.
     *
     * @param ctx       the draw context for the current frame
     * @param tickDelta partial ticks for smooth interpolation
     */
    public abstract void render(DrawContext ctx, float tickDelta);

    /**
     * Recomputes {@link #width} and {@link #height} based on the current
     * content of this element.  Called once after construction and whenever
     * the content changes significantly.
     */
    public abstract void updateSize();

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    public String getId()   { return id; }
    public String getName() { return name; }

    public int getX()       { return x; }
    public int getY()       { return y; }
    public int getWidth()   { return width; }
    public int getHeight()  { return height; }

    public boolean isVisible()  { return visible; }
    public boolean isDragging() { return dragging; }

    public void setVisible(boolean visible) { this.visible = visible; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    // -------------------------------------------------------------------------
    // Hit-test
    // -------------------------------------------------------------------------

    /**
     * Returns {@code true} if the given screen coordinate lies within the
     * axis-aligned bounding box of this element.
     */
    public boolean isHovered(int mx, int my) {
        return mx >= x && mx < x + width
                && my >= y && my < y + height;
    }

    // -------------------------------------------------------------------------
    // Drag support (used by HudEditor)
    // -------------------------------------------------------------------------

    /**
     * Called by the HUD editor when a mouse press is detected over this element.
     * Records the offset from the element's origin so the element can follow the
     * cursor accurately.
     */
    public void onMousePress(int mx, int my) {
        dragging    = true;
        dragOffsetX = mx - x;
        dragOffsetY = my - y;
    }

    /**
     * Called every frame while the user is dragging this element.
     * Updates the element's position to follow the mouse cursor.
     */
    public void onMouseDrag(int mx, int my) {
        if (dragging) {
            x = mx - dragOffsetX;
            y = my - dragOffsetY;
        }
    }

    /** Called when the mouse button is released to end a drag operation. */
    public void onMouseRelease() {
        dragging    = false;
        dragOffsetX = 0;
        dragOffsetY = 0;
    }

    // -------------------------------------------------------------------------
    // Serialization
    // -------------------------------------------------------------------------

    /**
     * Returns a {@link JsonObject} containing the position and visibility of
     * this element so it can be persisted to disk.
     */
    public JsonObject serializePos() {
        JsonObject obj = new JsonObject();
        obj.addProperty("x", x);
        obj.addProperty("y", y);
        obj.addProperty("visible", visible);
        return obj;
    }

    /**
     * Restores the position and visibility of this element from a previously
     * serialized {@link JsonObject}.
     */
    public void deserializePos(JsonObject obj) {
        if (obj.has("x"))       x       = obj.get("x").getAsInt();
        if (obj.has("y"))       y       = obj.get("y").getAsInt();
        if (obj.has("visible")) visible = obj.get("visible").getAsBoolean();
    }
}
