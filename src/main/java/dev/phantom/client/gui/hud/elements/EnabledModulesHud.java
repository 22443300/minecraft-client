package dev.phantom.client.gui.hud.elements;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.gui.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.Comparator;
import java.util.List;

/**
 * HUD element that renders the list of currently enabled modules aligned to
 * the right edge of the screen, sorted alphabetically.
 *
 * Each entry has a coloured left bar matching the module's category colour and
 * a semi-transparent dark background.
 */
public class EnabledModulesHud extends HudElement {

    private static final int PADDING    = 3;
    private static final int ENTRY_H    = 11;
    private static final int BAR_WIDTH  = 2;

    public EnabledModulesHud() {
        // Default position: top-right; actual X is computed dynamically each frame.
        super("enabled_modules", "Enabled Modules", 0, 4);
    }

    @Override
    public void updateSize() {
        MinecraftClient mc = MinecraftClient.getInstance();
        List<Module> enabled = getEnabled();
        if (enabled.isEmpty()) {
            width  = 0;
            height = 0;
            return;
        }
        int maxW = enabled.stream()
                .mapToInt(m -> mc.textRenderer.getWidth(m.getName()))
                .max()
                .orElse(60);
        width  = maxW + PADDING * 2 + BAR_WIDTH + 2;
        height = enabled.size() * ENTRY_H;
    }

    @Override
    public void render(DrawContext ctx, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        List<Module> enabled = getEnabled();
        if (enabled.isEmpty()) return;

        updateSize();

        // Dynamically anchor to right edge of screen
        int screenW = mc.getWindow().getScaledWidth();
        x = screenW - width - 2;

        int currentY = y;
        for (Module module : enabled) {
            int entryW = mc.textRenderer.getWidth(module.getName()) + PADDING * 2 + BAR_WIDTH + 2;

            // Background
            ctx.fill(x + width - entryW, currentY,
                    x + width, currentY + ENTRY_H, 0xAA000000);

            // Left category colour bar
            int catColor = module.getCategory().getColor();
            ctx.fill(x + width - entryW, currentY,
                    x + width - entryW + BAR_WIDTH, currentY + ENTRY_H,
                    catColor);

            // Module name
            ctx.drawText(mc.textRenderer, module.getName(),
                    x + width - entryW + BAR_WIDTH + PADDING,
                    currentY + (ENTRY_H - mc.textRenderer.fontHeight) / 2,
                    module.getCategory().getColor(), true);

            currentY += ENTRY_H;
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static List<Module> getEnabled() {
        return PhantomClient.INSTANCE.modules.getEnabled()
                .stream()
                .sorted(Comparator.comparing(Module::getName))
                .toList();
    }
}
