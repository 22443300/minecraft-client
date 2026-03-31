package dev.phantom.client.gui.theme;

import dev.phantom.client.core.module.Category;

public interface Theme {

    // -------------------------------------------------------------------------
    // Background / Surface Colors
    // -------------------------------------------------------------------------
    int BACKGROUND    = 0xEE0D0D0D;
    int SIDEBAR       = 0xEF080808;
    int PANEL         = 0xE5141414;
    int HEADER        = 0xFF1A1A1A;
    int ACCENT        = 0xFF2A2A2A;
    int BORDER        = 0xFF252525;

    // -------------------------------------------------------------------------
    // Text Colors
    // -------------------------------------------------------------------------
    int TEXT          = 0xFFE8E8E8;
    int TEXT_DIM      = 0xFF888888;
    int TEXT_DISABLED = 0xFF555555;

    // -------------------------------------------------------------------------
    // Toggle / Interactive Colors
    // -------------------------------------------------------------------------
    int TOGGLE_ON     = 0xFF4CAF50;
    int TOGGLE_OFF    = 0xFF555555;

    // -------------------------------------------------------------------------
    // Slider Colors
    // -------------------------------------------------------------------------
    int SLIDER_BG     = 0xFF2A2A2A;
    int SLIDER_FILL   = 0xFF4A9EFF;

    // -------------------------------------------------------------------------
    // Scrollbar
    // -------------------------------------------------------------------------
    int SCROLLBAR     = 0xFF333333;

    // -------------------------------------------------------------------------
    // Hover / Module States
    // -------------------------------------------------------------------------
    int HOVER         = 0xFF1F1F1F;
    int SEARCH_BG     = 0xFF1A1A1A;
    int MODULE_BG     = 0xFF141414;
    int MODULE_HOVER   = 0xFF1A1A1A;
    int MODULE_ENABLED = 0xFF1A2A1A;

    // -------------------------------------------------------------------------
    // Category Color Helper
    // -------------------------------------------------------------------------

    /**
     * Returns the accent color associated with a given {@link Category}.
     * Falls back to {@link #ACCENT} for unknown categories.
     */
    static int getCategoryColor(Category c) {
        if (c == null) return ACCENT;
        return c.getColor();
    }
}
