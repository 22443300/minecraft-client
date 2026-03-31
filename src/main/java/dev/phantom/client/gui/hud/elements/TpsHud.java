package dev.phantom.client.gui.hud.elements;

import dev.phantom.client.core.event.EventHandler;
import dev.phantom.client.core.event.events.TickEvent;
import dev.phantom.client.gui.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

/**
 * HUD element that estimates the server-side ticks-per-second (TPS).
 *
 * <p>TPS is estimated by measuring the time between consecutive
 * {@link TickEvent} dispatches.  The last {@code SAMPLE_SIZE} intervals are
 * averaged to smooth out noise.  The result is clamped to [0, 20].
 *
 * <p>The {@link HudManager} is responsible for subscribing this element to
 * the {@link dev.phantom.client.core.event.EventBus} during {@code init()}.
 */
public class TpsHud extends HudElement {

    // -------------------------------------------------------------------------
    // Tick-time sampling
    // -------------------------------------------------------------------------
    private static final int SAMPLE_SIZE = 20;

    private final long[] tickTimestamps = new long[SAMPLE_SIZE];
    private int sampleIndex = 0;
    private int sampleCount = 0;

    private float currentTps = 20f;

    // -------------------------------------------------------------------------
    // Rendering constants
    // -------------------------------------------------------------------------
    private static final int PADDING    = 4;
    private static final int BG_COLOR   = 0xAA000000;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public TpsHud() {
        super("tps", "TPS", 4, 50);
    }

    // -------------------------------------------------------------------------
    // Tick event handler
    // -------------------------------------------------------------------------

    /**
     * Called by the EventBus on every client tick.  Records the timestamp and
     * recalculates the rolling-average TPS.
     */
    @EventHandler
    public void onTick(TickEvent event) {
        onTick();
    }

    /**
     * Static-style entry point so {@link HudManager} (or {@code PhantomClient})
     * can drive this without an event if needed.
     */
    public void onTick() {
        long now = System.currentTimeMillis();
        tickTimestamps[sampleIndex] = now;
        sampleIndex = (sampleIndex + 1) % SAMPLE_SIZE;
        if (sampleCount < SAMPLE_SIZE) sampleCount++;

        if (sampleCount >= 2) {
            // Oldest sample index
            int oldest = (sampleIndex - sampleCount + SAMPLE_SIZE) % SAMPLE_SIZE;
            long totalTime = now - tickTimestamps[oldest];
            if (totalTime > 0) {
                float avgIntervalMs = (float) totalTime / (sampleCount - 1);
                currentTps = MathHelper.clamp(1000f / avgIntervalMs, 0f, 20f);
            }
        }
    }

    // -------------------------------------------------------------------------
    // HudElement implementation
    // -------------------------------------------------------------------------

    @Override
    public void updateSize() {
        MinecraftClient mc = MinecraftClient.getInstance();
        String text = buildText();
        int textW = mc.textRenderer != null ? mc.textRenderer.getWidth(text) : 60;
        width  = textW + PADDING * 2;
        height = mc.textRenderer != null ? mc.textRenderer.fontHeight + PADDING * 2 : 20;
    }

    @Override
    public void render(DrawContext ctx, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        String text = buildText();

        updateSize();

        ctx.fill(x, y, x + width, y + height, BG_COLOR);
        ctx.drawText(mc.textRenderer, text, x + PADDING, y + PADDING, tpsColor(currentTps), true);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private String buildText() {
        return "TPS: %.1f".formatted(currentTps);
    }

    private static int tpsColor(float tps) {
        if (tps >= 18f) return 0xFF4CAF50; // green – near perfect
        if (tps >= 12f) return 0xFFFFEB3B; // yellow – noticeable lag
        return 0xFFF44336;                  // red – severe lag
    }
}
