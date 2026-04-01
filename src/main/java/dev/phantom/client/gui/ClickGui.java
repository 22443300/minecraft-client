package dev.phantom.client.gui;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.BooleanSetting;
import dev.phantom.client.core.module.setting.ColorSetting;
import dev.phantom.client.core.module.setting.DoubleSetting;
import dev.phantom.client.core.module.setting.EnumSetting;
import dev.phantom.client.core.module.setting.IntSetting;
import dev.phantom.client.core.module.setting.ModeSetting;
import dev.phantom.client.core.module.setting.Setting;
import dev.phantom.client.core.module.setting.StringSetting;
import dev.phantom.client.gui.editor.FriendListScreen;
import dev.phantom.client.gui.editor.KeybindEditorScreen;
import dev.phantom.client.gui.editor.MacroEditorScreen;
import dev.phantom.client.gui.editor.ProfileSwitcherScreen;
import dev.phantom.client.gui.hud.HudEditor;
import dev.phantom.client.gui.theme.Theme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * Main ClickGui screen for the Phantom Client.
 *
 * Layout (all units in pixels):
 * <pre>
 *  ┌──────────────────────────────────────────┐
 *  │             TOP BAR  (32px)              │
 *  ├──────────┬───────────────────────────────┤
 *  │          │                               │
 *  │ SIDEBAR  │       MODULE PANEL            │
 *  │ (100px)  │                               │
 *  ├──────────┴───────────────────────────────┤
 *  │            BOTTOM BAR (30px)             │
 *  └──────────────────────────────────────────┘
 * </pre>
 */
public class ClickGui extends Screen {

    // -------------------------------------------------------------------------
    // Layout constants
    // -------------------------------------------------------------------------
    private static final int SIDEBAR_WIDTH       = 100;
    private static final int TOP_BAR_HEIGHT      = 32;
    private static final int BOTTOM_BAR_HEIGHT   = 30;
    private static final int MODULE_HEIGHT       = 22;
    private static final int SETTINGS_ITEM_HEIGHT = 20;
    private static final int PADDING             = 6;

    // -------------------------------------------------------------------------
    // State
    // -------------------------------------------------------------------------
    private Category selectedCategory = Category.COMBAT;
    private final List<Module> searchResults = new ArrayList<>();
    private String searchText = "";
    private int scrollOffset = 0;
    private int expandedModule = -1;   // index in current visible list; -1 = none
    private float animProgress = 0f;
    private int moduleListScroll = 0;

    // Slider interaction state
    private boolean isDraggingSlider = false;
    private int draggingSettingIndex = -1;
    private int draggingModuleIndex  = -1;

    // Search field widget
    private TextFieldWidget searchField;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
    public ClickGui() {
        super(Text.literal("Phantom ClickGui"));
    }

    // -------------------------------------------------------------------------
    // Screen lifecycle
    // -------------------------------------------------------------------------

    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    protected void init() {
        super.init();

        int fieldX = SIDEBAR_WIDTH + PADDING;
        int fieldY = (TOP_BAR_HEIGHT - 14) / 2;
        int fieldW = Math.min(160, width - SIDEBAR_WIDTH - PADDING * 2);

        searchField = new TextFieldWidget(
                textRenderer,
                fieldX, fieldY, fieldW, 14,
                Text.literal("Search...")
        );
        searchField.setMaxLength(64);
        searchField.setDrawsBackground(false);
        searchField.setPlaceholder(Text.literal("Search..."));
        searchField.setText(searchText);
        searchField.setChangedListener(this::onSearchChanged);
        addDrawableChild(searchField);
    }

    private void onSearchChanged(String text) {
        searchText = text.trim();
        scrollOffset = 0;
        moduleListScroll = 0;
        expandedModule = -1;

        searchResults.clear();
        if (!searchText.isEmpty()) {
            String lower = searchText.toLowerCase();
            for (Module m : PhantomClient.INSTANCE.modules.getModules()) {
                if (m.getName().toLowerCase().contains(lower)) {
                    searchResults.add(m);
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Render
    // -------------------------------------------------------------------------

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 1. Full dim background
        context.fill(0, 0, width, height, 0xBB000000);

        // 2. Top bar
        context.fill(0, 0, width, TOP_BAR_HEIGHT, Theme.HEADER);
        // Title
        context.drawText(textRenderer, "PHANTOM", PADDING, (TOP_BAR_HEIGHT - textRenderer.fontHeight) / 2, Theme.TEXT, false);

        // Search background
        int sfBgX = SIDEBAR_WIDTH + PADDING - 2;
        int sfBgY = (TOP_BAR_HEIGHT - 18) / 2;
        int sfBgW = searchField.getWidth() + 4;
        context.fill(sfBgX, sfBgY, sfBgX + sfBgW, sfBgY + 18, Theme.SEARCH_BG);

        // 3. Sidebar background
        context.fill(0, TOP_BAR_HEIGHT, SIDEBAR_WIDTH, height - BOTTOM_BAR_HEIGHT, Theme.SIDEBAR);
        // Sidebar separator
        context.fill(SIDEBAR_WIDTH, TOP_BAR_HEIGHT, SIDEBAR_WIDTH + 1, height - BOTTOM_BAR_HEIGHT, Theme.BORDER);

        // 4. Module panel background
        context.fill(SIDEBAR_WIDTH + 1, TOP_BAR_HEIGHT, width, height - BOTTOM_BAR_HEIGHT, Theme.PANEL);

        // 5. Bottom bar
        context.fill(0, height - BOTTOM_BAR_HEIGHT, width, height, Theme.HEADER);
        // Bottom separator
        context.fill(0, height - BOTTOM_BAR_HEIGHT, width, height - BOTTOM_BAR_HEIGHT + 1, Theme.BORDER);

        // 6. Category list in sidebar
        renderCategoryList(context, mouseX, mouseY);

        // 7. Module list in main panel
        renderModuleList(context, mouseX, mouseY);

        // 8. Bottom bar tabs
        renderBottomBar(context, mouseX, mouseY);

        // 9. Draw search field widget (via super chain)
        super.render(context, mouseX, mouseY, delta);
    }

    // -------------------------------------------------------------------------
    // Sidebar / Category rendering
    // -------------------------------------------------------------------------

    private void renderCategoryList(DrawContext context, int mouseX, int mouseY) {
        Category[] cats = Category.values();
        int tabH = 24;
        int startY = TOP_BAR_HEIGHT + PADDING;

        for (int i = 0; i < cats.length; i++) {
            Category cat = cats[i];
            int tabY = startY + i * (tabH + 2);
            boolean selected = (cat == selectedCategory && searchText.isEmpty());
            boolean hovered = mouseX >= 0 && mouseX < SIDEBAR_WIDTH && mouseY >= tabY && mouseY < tabY + tabH;
            renderCategoryTab(context, cat, 0, tabY, SIDEBAR_WIDTH, tabH, selected, hovered);
        }
    }

    private void renderCategoryTab(DrawContext context, Category cat, int x, int y, int w, int h,
                                   boolean selected, boolean hovered) {
        int bg = selected ? Theme.HOVER : (hovered ? 0xFF111111 : Theme.SIDEBAR);
        context.fill(x, y, x + w, y + h, bg);

        // Left color stripe
        int stripeColor = selected ? cat.getColor() : (hovered ? cat.getColor() & 0x88FFFFFF : cat.getColor() & 0x44FFFFFF);
        context.fill(x, y, x + 3, y + h, stripeColor);

        // Category name
        int textColor = selected ? Theme.TEXT : (hovered ? Theme.TEXT_DIM : Theme.TEXT_DISABLED);
        String name = cat.getDisplayName();
        int textX = x + 8;
        int textY = y + (h - textRenderer.fontHeight) / 2;
        context.drawText(textRenderer, name, textX, textY, textColor, false);
    }

    // -------------------------------------------------------------------------
    // Module list rendering
    // -------------------------------------------------------------------------

    private List<Module> getVisibleModules() {
        if (!searchText.isEmpty()) {
            return searchResults;
        }
        return PhantomClient.INSTANCE.modules.getByCategory(selectedCategory);
    }

    private void renderModuleList(DrawContext context, int mouseX, int mouseY) {
        List<Module> modules = getVisibleModules();
        if (modules == null || modules.isEmpty()) return;

        int panelX = SIDEBAR_WIDTH + 1;
        int panelY = TOP_BAR_HEIGHT;
        int panelW = width - panelX;
        int panelH = height - TOP_BAR_HEIGHT - BOTTOM_BAR_HEIGHT;

        // Enable scissor/clip for module panel
        context.enableScissor(panelX, panelY, panelX + panelW, panelY + panelH);

        int currentY = panelY + PADDING - moduleListScroll;
        int modW = panelW - PADDING * 2;

        for (int i = 0; i < modules.size(); i++) {
            Module module = modules.get(i);
            if (currentY + MODULE_HEIGHT < panelY) {
                // Accumulate height even if off-screen top
                currentY += MODULE_HEIGHT;
                if (expandedModule == i) {
                    currentY += computeSettingsHeight(module);
                }
                continue;
            }
            if (currentY > panelY + panelH) break;

            boolean hovered = mouseX >= panelX + PADDING && mouseX < panelX + PADDING + modW
                    && mouseY >= currentY && mouseY < currentY + MODULE_HEIGHT;
            boolean expanded = (expandedModule == i);

            renderModule(context, module, panelX + PADDING, currentY, modW, hovered, expanded);
            currentY += MODULE_HEIGHT;

            if (expanded) {
                int settingsH = renderSettings(context, module, panelX + PADDING, currentY, modW, mouseX, mouseY);
                currentY += settingsH;
            }
        }

        context.disableScissor();
    }

    private void renderModule(DrawContext context, Module module, int x, int y, int w,
                              boolean hovered, boolean expanded) {
        // Background
        int bg;
        if (module.isEnabled()) {
            bg = hovered ? 0xFF1E321E : Theme.MODULE_ENABLED;
        } else {
            bg = hovered ? Theme.MODULE_HOVER : Theme.MODULE_BG;
        }
        context.fill(x, y, x + w, y + MODULE_HEIGHT, bg);

        // Left category color bar (3px)
        int catColor = module.getCategory().getColor();
        context.fill(x, y, x + 3, y + MODULE_HEIGHT, catColor);

        // Module name
        int nameColor = module.isEnabled() ? Theme.TEXT : Theme.TEXT_DIM;
        context.drawText(textRenderer, module.getName(),
                x + 7, y + (MODULE_HEIGHT - textRenderer.fontHeight) / 2, nameColor, false);

        // Toggle indicator (right side, 8x8 square)
        int toggleColor = module.isEnabled() ? Theme.TOGGLE_ON : Theme.TOGGLE_OFF;
        int toggleSize  = 8;
        int toggleX     = x + w - toggleSize - 5;
        int toggleY     = y + (MODULE_HEIGHT - toggleSize) / 2;
        context.fill(toggleX, toggleY, toggleX + toggleSize, toggleY + toggleSize, toggleColor);

        // Expanded indicator arrow
        if (expanded) {
            context.drawText(textRenderer, "▼", x + w - toggleSize - 18,
                    y + (MODULE_HEIGHT - textRenderer.fontHeight) / 2, Theme.TEXT_DIM, false);
        } else if (!module.getSettings().isEmpty()) {
            context.drawText(textRenderer, "▶", x + w - toggleSize - 18,
                    y + (MODULE_HEIGHT - textRenderer.fontHeight) / 2, Theme.TEXT_DISABLED, false);
        }

        // Bottom separator
        context.fill(x, y + MODULE_HEIGHT - 1, x + w, y + MODULE_HEIGHT, Theme.BORDER);
    }

    // -------------------------------------------------------------------------
    // Settings rendering
    // -------------------------------------------------------------------------

    /**
     * Renders the settings panel for a module immediately below its row.
     *
     * @return total height consumed (including the bottom padding)
     */
    private int renderSettings(DrawContext context, Module module, int x, int y, int w,
                               int mouseX, int mouseY) {
        List<Setting<?>> settings = module.getSettings();
        if (settings.isEmpty()) return 0;

        // Settings background
        int totalH = settings.stream()
                .filter(Setting::isVisible)
                .mapToInt(s -> SETTINGS_ITEM_HEIGHT)
                .sum() + PADDING * 2;

        context.fill(x, y, x + w, y + totalH, 0xF0101010);
        // Left accent line matching category
        context.fill(x, y, x + 2, y + totalH, module.getCategory().getColor() & 0x88FFFFFF);

        int itemY = y + PADDING;
        int settingIndex = 0;

        for (Setting<?> setting : settings) {
            if (!setting.isVisible()) {
                settingIndex++;
                continue;
            }

            boolean settingHovered = mouseX >= x && mouseX < x + w
                    && mouseY >= itemY && mouseY < itemY + SETTINGS_ITEM_HEIGHT;

            if (setting instanceof BooleanSetting bs) {
                renderBooleanSetting(context, bs, x + 4, itemY, w - 8, settingHovered);
            } else if (setting instanceof IntSetting is) {
                renderIntSetting(context, is, x + 4, itemY, w - 8, settingHovered);
            } else if (setting instanceof DoubleSetting ds) {
                renderDoubleSetting(context, ds, x + 4, itemY, w - 8, settingHovered);
            } else if (setting instanceof EnumSetting<?> es) {
                renderEnumSetting(context, es, x + 4, itemY, w - 8, settingHovered, mouseX, mouseY);
            } else if (setting instanceof ModeSetting ms) {
                renderModeSetting(context, ms, x + 4, itemY, w - 8, settingHovered, mouseX, mouseY);
            } else if (setting instanceof StringSetting ss) {
                renderStringSetting(context, ss, x + 4, itemY, w - 8, settingHovered);
            } else if (setting instanceof ColorSetting cs) {
                renderColorSetting(context, cs, x + 4, itemY, w - 8, settingHovered);
            } else {
                // Generic fallback: just show name: value
                String label = setting.getName() + ": " + setting.getValue();
                context.drawText(textRenderer, label,
                        x + 6, itemY + (SETTINGS_ITEM_HEIGHT - textRenderer.fontHeight) / 2,
                        Theme.TEXT_DIM, false);
            }

            itemY += SETTINGS_ITEM_HEIGHT;
            settingIndex++;
        }

        return totalH;
    }

    /** Computes the settings panel height without rendering. */
    private int computeSettingsHeight(Module module) {
        List<Setting<?>> settings = module.getSettings();
        if (settings.isEmpty()) return 0;
        int visible = (int) settings.stream().filter(Setting::isVisible).count();
        return visible * SETTINGS_ITEM_HEIGHT + PADDING * 2;
    }

    // --- Individual setting renderers ---

    private void renderBooleanSetting(DrawContext ctx, BooleanSetting setting,
                                      int x, int y, int w, boolean hovered) {
        String name = setting.getName();
        ctx.drawText(textRenderer, name, x + 2, y + (SETTINGS_ITEM_HEIGHT - textRenderer.fontHeight) / 2,
                hovered ? Theme.TEXT : Theme.TEXT_DIM, false);

        boolean val = setting.getValue();
        int toggleW = 24, toggleH = 10;
        int toggleX = x + w - toggleW - 2;
        int toggleY = y + (SETTINGS_ITEM_HEIGHT - toggleH) / 2;

        // Track BG
        int trackColor = val ? Theme.TOGGLE_ON : Theme.TOGGLE_OFF;
        ctx.fill(toggleX, toggleY, toggleX + toggleW, toggleY + toggleH, trackColor);

        // Knob
        int knobSize = toggleH - 2;
        int knobX    = val ? (toggleX + toggleW - knobSize - 1) : (toggleX + 1);
        int knobY    = toggleY + 1;
        ctx.fill(knobX, knobY, knobX + knobSize, knobY + knobSize, Theme.TEXT);
    }

    private void renderIntSetting(DrawContext ctx, IntSetting setting,
                                  int x, int y, int w, boolean hovered) {
        String label = setting.getName() + ": " + setting.getValue();
        ctx.drawText(textRenderer, label, x + 2, y + (SETTINGS_ITEM_HEIGHT - textRenderer.fontHeight) / 2,
                hovered ? Theme.TEXT : Theme.TEXT_DIM, false);

        int sliderW = Math.min(80, w / 2);
        int sliderH = 6;
        int sliderX = x + w - sliderW - 2;
        int sliderY = y + (SETTINGS_ITEM_HEIGHT - sliderH) / 2;

        ctx.fill(sliderX, sliderY, sliderX + sliderW, sliderY + sliderH, Theme.SLIDER_BG);

        float pct = (float)(setting.getValue() - setting.getMin()) / (setting.getMax() - setting.getMin());
        int fillW  = Math.round(pct * sliderW);
        ctx.fill(sliderX, sliderY, sliderX + fillW, sliderY + sliderH, Theme.SLIDER_FILL);

        // Thumb
        int thumbX = sliderX + fillW - 2;
        ctx.fill(thumbX, sliderY - 1, thumbX + 4, sliderY + sliderH + 1, Theme.TEXT);
    }

    private void renderDoubleSetting(DrawContext ctx, DoubleSetting setting,
                                     int x, int y, int w, boolean hovered) {
        String label = setting.getName() + ": " + String.format("%.2f", setting.getValue());
        ctx.drawText(textRenderer, label, x + 2, y + (SETTINGS_ITEM_HEIGHT - textRenderer.fontHeight) / 2,
                hovered ? Theme.TEXT : Theme.TEXT_DIM, false);

        int sliderW = Math.min(80, w / 2);
        int sliderH = 6;
        int sliderX = x + w - sliderW - 2;
        int sliderY = y + (SETTINGS_ITEM_HEIGHT - sliderH) / 2;

        ctx.fill(sliderX, sliderY, sliderX + sliderW, sliderY + sliderH, Theme.SLIDER_BG);

        float pct = (float)((setting.getValue() - setting.getMin()) / (setting.getMax() - setting.getMin()));
        int fillW  = Math.round(pct * sliderW);
        ctx.fill(sliderX, sliderY, sliderX + fillW, sliderY + sliderH, Theme.SLIDER_FILL);

        int thumbX = sliderX + fillW - 2;
        ctx.fill(thumbX, sliderY - 1, thumbX + 4, sliderY + sliderH + 1, Theme.TEXT);
    }

    private void renderEnumSetting(DrawContext ctx, EnumSetting<?> setting,
                                   int x, int y, int w, boolean hovered, int mx, int my) {
        String valueStr = setting.getValue().toString();
        int arrowW = 10;

        // Left arrow
        boolean leftHov = mx >= x && mx < x + arrowW && my >= y && my < y + SETTINGS_ITEM_HEIGHT;
        ctx.drawText(textRenderer, "<", x + 1, y + (SETTINGS_ITEM_HEIGHT - textRenderer.fontHeight) / 2,
                leftHov ? Theme.TEXT : Theme.TEXT_DIM, false);

        // Name: Value (centered)
        String label = setting.getName() + ": " + valueStr;
        int labelW = textRenderer.getWidth(label);
        int labelX = x + (w - labelW) / 2;
        ctx.drawText(textRenderer, label, labelX, y + (SETTINGS_ITEM_HEIGHT - textRenderer.fontHeight) / 2,
                hovered ? Theme.TEXT : Theme.TEXT_DIM, false);

        // Right arrow
        boolean rightHov = mx >= x + w - arrowW && mx < x + w && my >= y && my < y + SETTINGS_ITEM_HEIGHT;
        ctx.drawText(textRenderer, ">", x + w - arrowW, y + (SETTINGS_ITEM_HEIGHT - textRenderer.fontHeight) / 2,
                rightHov ? Theme.TEXT : Theme.TEXT_DIM, false);
    }

    private void renderModeSetting(DrawContext ctx, ModeSetting setting,
                                   int x, int y, int w, boolean hovered, int mx, int my) {
        int arrowW = 10;

        boolean leftHov  = mx >= x && mx < x + arrowW && my >= y && my < y + SETTINGS_ITEM_HEIGHT;
        boolean rightHov = mx >= x + w - arrowW && mx < x + w && my >= y && my < y + SETTINGS_ITEM_HEIGHT;

        ctx.drawText(textRenderer, "<", x + 1, y + (SETTINGS_ITEM_HEIGHT - textRenderer.fontHeight) / 2,
                leftHov ? Theme.TEXT : Theme.TEXT_DIM, false);

        String label = setting.getName() + ": " + setting.getValue();
        int labelW = textRenderer.getWidth(label);
        int labelX = x + (w - labelW) / 2;
        ctx.drawText(textRenderer, label, labelX, y + (SETTINGS_ITEM_HEIGHT - textRenderer.fontHeight) / 2,
                hovered ? Theme.TEXT : Theme.TEXT_DIM, false);

        ctx.drawText(textRenderer, ">", x + w - arrowW, y + (SETTINGS_ITEM_HEIGHT - textRenderer.fontHeight) / 2,
                rightHov ? Theme.TEXT : Theme.TEXT_DIM, false);
    }

    private void renderStringSetting(DrawContext ctx, StringSetting setting,
                                     int x, int y, int w, boolean hovered) {
        String label = setting.getName() + ": " + setting.getValue();
        ctx.fill(x, y + 2, x + w, y + SETTINGS_ITEM_HEIGHT - 2, Theme.SEARCH_BG);
        ctx.drawText(textRenderer, label, x + 4, y + (SETTINGS_ITEM_HEIGHT - textRenderer.fontHeight) / 2,
                hovered ? Theme.TEXT : Theme.TEXT_DIM, false);
    }

    private void renderColorSetting(DrawContext ctx, ColorSetting setting,
                                    int x, int y, int w, boolean hovered) {
        ctx.drawText(textRenderer, setting.getName(),
                x + 2, y + (SETTINGS_ITEM_HEIGHT - textRenderer.fontHeight) / 2,
                hovered ? Theme.TEXT : Theme.TEXT_DIM, false);

        // Color preview square
        int squareSize = 12;
        int squareX = x + w - squareSize - 2;
        int squareY = y + (SETTINGS_ITEM_HEIGHT - squareSize) / 2;
        ctx.fill(squareX, squareY, squareX + squareSize, squareY + squareSize, setting.getValue());
        // Border around color square
        ctx.fill(squareX - 1, squareY - 1, squareX + squareSize + 1, squareY, Theme.BORDER);
        ctx.fill(squareX - 1, squareY + squareSize, squareX + squareSize + 1, squareY + squareSize + 1, Theme.BORDER);
        ctx.fill(squareX - 1, squareY, squareX, squareY + squareSize, Theme.BORDER);
        ctx.fill(squareX + squareSize, squareY, squareX + squareSize + 1, squareY + squareSize, Theme.BORDER);
    }

    // -------------------------------------------------------------------------
    // Bottom bar
    // -------------------------------------------------------------------------

    private static final String[] BOTTOM_TABS = {"Keybinds", "Macros", "Friends", "Profiles", "HUD Edit"};

    private void renderBottomBar(DrawContext context, int mouseX, int mouseY) {
        int tabX = PADDING;
        int barY = height - BOTTOM_BAR_HEIGHT;
        int tabH = BOTTOM_BAR_HEIGHT;

        for (String tab : BOTTOM_TABS) {
            int tabW = textRenderer.getWidth(tab) + PADDING * 2;
            boolean hovered = mouseX >= tabX && mouseX < tabX + tabW
                    && mouseY >= barY && mouseY < barY + tabH;

            if (hovered) {
                context.fill(tabX, barY + 2, tabX + tabW, barY + tabH - 2, Theme.HOVER);
            }
            context.drawText(textRenderer, tab,
                    tabX + PADDING, barY + (tabH - textRenderer.fontHeight) / 2,
                    hovered ? Theme.TEXT : Theme.TEXT_DIM, false);

            tabX += tabW + 4;
        }
    }

    // -------------------------------------------------------------------------
    // Input handling
    // -------------------------------------------------------------------------

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        int imx = (int) mx;
        int imy = (int) my;

        // --- Sidebar category clicks ---
        if (imx >= 0 && imx < SIDEBAR_WIDTH
                && imy >= TOP_BAR_HEIGHT && imy < height - BOTTOM_BAR_HEIGHT) {
            Category[] cats = Category.values();
            int tabH  = 24;
            int startY = TOP_BAR_HEIGHT + PADDING;
            for (int i = 0; i < cats.length; i++) {
                int tabY = startY + i * (tabH + 2);
                if (imy >= tabY && imy < tabY + tabH) {
                    selectedCategory = cats[i];
                    searchField.setText("");
                    searchText = "";
                    searchResults.clear();
                    expandedModule = -1;
                    moduleListScroll = 0;
                    return true;
                }
            }
        }

        // --- Bottom bar clicks ---
        if (imy >= height - BOTTOM_BAR_HEIGHT) {
            int tabX = PADDING;
            for (String tab : BOTTOM_TABS) {
                int tabW = textRenderer.getWidth(tab) + PADDING * 2;
                if (imx >= tabX && imx < tabX + tabW) {
                    handleBottomTabClick(tab);
                    return true;
                }
                tabX += tabW + 4;
            }
        }

        // --- Module panel clicks ---
        int panelX = SIDEBAR_WIDTH + 1;
        int panelW = width - panelX;
        if (imx >= panelX && imx < panelX + panelW
                && imy >= TOP_BAR_HEIGHT && imy < height - BOTTOM_BAR_HEIGHT) {

            List<Module> modules = getVisibleModules();
            if (modules == null || modules.isEmpty()) {
                return super.mouseClicked(mx, my, button);
            }

            int currentY = TOP_BAR_HEIGHT + PADDING - moduleListScroll;
            int modW     = panelW - PADDING * 2;
            int modX     = panelX + PADDING;

            for (int i = 0; i < modules.size(); i++) {
                Module module = modules.get(i);

                // Module row click
                if (imx >= modX && imx < modX + modW
                        && imy >= currentY && imy < currentY + MODULE_HEIGHT) {

                    if (button == 0) {
                        // Toggle expand/collapse on left click + check toggle zone
                        int toggleSize = 8;
                        int toggleX    = modX + modW - toggleSize - 5;
                        if (imx >= toggleX - 2 && imx <= modX + modW - 2) {
                            // Clicked toggle area - toggle module
                            module.toggle();
                        } else {
                            // Clicked name area - toggle expand
                            expandedModule = (expandedModule == i) ? -1 : i;
                        }
                        return true;
                    } else if (button == 1) {
                        // Right click - toggle module
                        module.toggle();
                        return true;
                    }
                }
                currentY += MODULE_HEIGHT;

                // Settings area click
                if (expandedModule == i) {
                    int settingsH = computeSettingsHeight(module);
                    if (imy >= currentY && imy < currentY + settingsH) {
                        handleSettingClick(module, modX + 4, currentY + PADDING, modW - 8, imx, imy, button);
                        return true;
                    }
                    currentY += settingsH;
                }

                if (currentY > height - BOTTOM_BAR_HEIGHT) break;
            }
        }

        return super.mouseClicked(mx, my, button);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void handleSettingClick(Module module, int x, int startY, int w, int mx, int my, int button) {
        List<Setting<?>> settings = module.getSettings();
        int itemY = startY;

        for (Setting<?> setting : settings) {
            if (!setting.isVisible()) continue;

            if (my >= itemY && my < itemY + SETTINGS_ITEM_HEIGHT) {
                if (setting instanceof BooleanSetting bs) {
                    bs.setValue(!bs.getValue());

                } else if (setting instanceof IntSetting is) {
                    int sliderW = Math.min(80, w / 2);
                    int sliderX = x + w - sliderW - 2;
                    if (mx >= sliderX && mx <= sliderX + sliderW) {
                        float pct = (float)(mx - sliderX) / sliderW;
                        int val = is.getMin() + Math.round(pct * (is.getMax() - is.getMin()));
                        is.setValue(val);
                        isDraggingSlider = true;
                        draggingModuleIndex = expandedModule;
                        draggingSettingIndex = settings.indexOf(setting);
                    }

                } else if (setting instanceof DoubleSetting ds) {
                    int sliderW = Math.min(80, w / 2);
                    int sliderX = x + w - sliderW - 2;
                    if (mx >= sliderX && mx <= sliderX + sliderW) {
                        float pct = (float)(mx - sliderX) / sliderW;
                        double val = ds.getMin() + pct * (ds.getMax() - ds.getMin());
                        ds.setValue(val);
                        isDraggingSlider = true;
                        draggingModuleIndex = expandedModule;
                        draggingSettingIndex = settings.indexOf(setting);
                    }

                } else if (setting instanceof EnumSetting es) {
                    int arrowW = 10;
                    if (mx >= x && mx < x + arrowW) {
                        es.prev();
                    } else if (mx >= x + w - arrowW && mx < x + w) {
                        es.next();
                    }

                } else if (setting instanceof ModeSetting ms) {
                    int arrowW = 10;
                    if (mx >= x && mx < x + arrowW) {
                        ms.prev();
                    } else if (mx >= x + w - arrowW && mx < x + w) {
                        ms.next();
                    }
                }
                return;
            }
            itemY += SETTINGS_ITEM_HEIGHT;
        }
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double deltaX, double deltaY) {
        if (isDraggingSlider && expandedModule >= 0) {
            List<Module> modules = getVisibleModules();
            if (modules != null && draggingModuleIndex < modules.size()) {
                Module module = modules.get(draggingModuleIndex);
                List<Setting<?>> settings = module.getSettings();
                if (draggingSettingIndex >= 0 && draggingSettingIndex < settings.size()) {
                    Setting<?> setting = settings.get(draggingSettingIndex);
                    int panelX = SIDEBAR_WIDTH + 1;
                    int panelW = width - panelX;
                    int modW   = panelW - PADDING * 2;
                    int settingW = modW - 8;
                    int sliderW  = Math.min(80, settingW / 2);
                    int sliderX  = panelX + PADDING + 4 + settingW - sliderW - 2;

                    float pct = MathHelper.clamp((float)(mx - sliderX) / sliderW, 0f, 1f);

                    if (setting instanceof IntSetting is) {
                        int val = is.getMin() + Math.round(pct * (is.getMax() - is.getMin()));
                        is.setValue(val);
                    } else if (setting instanceof DoubleSetting ds) {
                        double val = ds.getMin() + pct * (ds.getMax() - ds.getMin());
                        ds.setValue(val);
                    }
                }
            }
        }
        return super.mouseDragged(mx, my, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        isDraggingSlider = false;
        draggingSettingIndex = -1;
        draggingModuleIndex  = -1;
        return super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double hScroll, double vScroll) {
        int panelX = SIDEBAR_WIDTH + 1;
        if (mx >= panelX && my >= TOP_BAR_HEIGHT && my < height - BOTTOM_BAR_HEIGHT) {
            moduleListScroll -= (int)(vScroll * 10);
            moduleListScroll = Math.max(0, moduleListScroll);
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int mods) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, mods);
    }

    // -------------------------------------------------------------------------
    // Bottom tab actions
    // -------------------------------------------------------------------------

    private void handleBottomTabClick(String tab) {
        MinecraftClient mc = MinecraftClient.getInstance();
        switch (tab) {
            case "HUD Edit"  -> mc.setScreen(new HudEditor());
            case "Macros"    -> mc.setScreen(new MacroEditorScreen());
            case "Friends"   -> mc.setScreen(new FriendListScreen());
            case "Profiles"  -> mc.setScreen(new ProfileSwitcherScreen());
            case "Keybinds"  -> mc.setScreen(new KeybindEditorScreen());
            default -> { /* no-op */ }
        }
    }
}
